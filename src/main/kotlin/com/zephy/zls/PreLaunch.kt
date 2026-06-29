package com.zephy.zls

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

private const val RESOURCE_FOLDER = "modules"
private const val ZJS_DIR = "ZJS"

private val LOGGER = LoggerFactory.getLogger("zls")
private val LOG_PREFIX = "[ZLS]"
private fun logInfo(message: String) = LOGGER.info("$LOG_PREFIX $message")
private fun logWarn(message: String, cause: Throwable? = null) =
    if (cause != null) LOGGER.warn("$LOG_PREFIX $message", cause) else LOGGER.warn("$LOG_PREFIX $message")

fun versionToInt(version: String): Int {
    val parts = version.split(".").map { it.toInt() }
    val major = parts[0]
    val minor = parts.getOrElse(1) { 0 }
    val patch = parts.getOrElse(2) { 0 }
    return "$major${minor.toString().padStart(2, '0')}${patch.toString().padStart(2, '0')}".toInt()
}

private data class ModuleMetadata(val version: String = "")

class PreLaunch : PreLaunchEntrypoint {
    val RESOURCE_DIR: Path = FabricLoader.getInstance().configDir
        .resolve(ZJS_DIR)
        .resolve(RESOURCE_FOLDER)

    override fun onPreLaunch() {
        logInfo("Pre-Launch started.")
        copyDefaultConfigs()
    }

    private fun copyDefaultConfigs() {
        val resourceUri = javaClass.getResource("/$RESOURCE_FOLDER")?.toURI()
            ?: return logWarn("Resource folder '/$RESOURCE_FOLDER' not found, skipping.")

        runCatching {
            if (resourceUri.scheme == "jar") {
                FileSystems.newFileSystem(resourceUri, emptyMap<String, Any>()).use { fs ->
                    copyAllModules(fs.getPath("/$RESOURCE_FOLDER"))
                }
            } else {
                copyAllModules(Path.of(resourceUri))
            }
        }.onFailure { e ->
            if (e is IOException) logWarn("Failed to copy default configs.", e)
            else throw e
        }.onSuccess { copiedFileCount ->
            if (copiedFileCount > 0) reloadZJSModules()
        }
    }

    private fun copyAllModules(allModulesPath: Path): Int {
        return Files.list(allModulesPath).use { stream ->
            stream
                .filter(Files::isDirectory)
                .mapToInt { modulePath ->
                    copyResources(modulePath, RESOURCE_DIR.resolve(modulePath.fileName.toString()))
                }
                .sum()
        }
    }

    private fun copyResources(modulePath: Path, destDir: Path): Int {
        val folderName = modulePath.fileName.toString()
        val existingVersionInt = getZJSModuleVersion(destDir)
        val moduleVersionInt = getZJSModuleVersion(modulePath)

        val copiedCount = Files.walk(modulePath).use { stream ->
            stream
                .filter(Files::isRegularFile)
                .mapToInt { source ->
                    val destination = destDir.resolve(modulePath.relativize(source).toString())
                    if (Files.notExists(destination) || moduleVersionInt > existingVersionInt) {
                        Files.createDirectories(destination.parent)
                        Files.copy(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
                        1
                    } else 0
                }
                .sum()
        }

        if (copiedCount > 0) logInfo("[$folderName] Install complete. Copied $copiedCount file(s) to $destDir.")
        return copiedCount
    }

    private fun getZJSModuleVersion(modulePath: Path): Int {
        return try {
            val metaFile = modulePath.resolve("metadata.json")
            val version = Gson().fromJson(Files.readString(metaFile), ModuleMetadata::class.java).version
            if (version.isEmpty()) 0 else versionToInt(version)
        } catch (_: Exception) {
            0
        }
    }

    private fun reloadZJSModules() {
        if (!FabricLoader.getInstance().isModLoaded("zjs")) return
        runCatching {
            val clazz = Class.forName("com.zephy.zjs.internal.engine.module.ModuleManager")
            val instance = clazz.getField("INSTANCE").get(null)
            val method = clazz.getDeclaredMethod("setup")
            method.invoke(instance)
        }.onFailure { e ->
            val cause = (e as? java.lang.reflect.InvocationTargetException)?.cause ?: e
            logWarn("Failed to invoke ModuleManager.setup: ${cause::class.java.name}: ${cause.message}", cause)
        }
    }
}
