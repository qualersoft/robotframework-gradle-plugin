package de.qualersoft.robotframework.gradleplugin.harvester

interface NameHarvester {
  fun harvest(pattern: String): Set<String>
}