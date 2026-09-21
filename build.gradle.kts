plugins {
	id("fabric-loom") version "1.14-SNAPSHOT"
	id("java")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
	archivesName.set("truealwayssprint")
}

repositories {
	mavenCentral()
	maven("https://maven.shedaniel.me/")
	maven("https://maven.terraformersmc.com/")
}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
	modApi("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_config_version")}")
	modApi("com.terraformersmc:modmenu:${property("mod_menu_version")}")
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register("truealwayssprint") {
			sourceSet("main")
			sourceSet("client")
		}
	}
}

tasks.processResources {
	inputs.property("version", project.version)
	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}