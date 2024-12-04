DROP TABLE IF EXISTS lutecetools_component_dependency;
CREATE TABLE `lutecetools_component_dependency` (
  `component_artifactId` varchar(100) NOT NULL,
  `dependency_artifactId` varchar(100) NOT NULL,
  `dependency_version` varchar(50),
  PRIMARY KEY (`component_artifactId`,`dependency_artifactId`)
) ;

