
INSERT INTO core_datastore(entity_key, entity_value) VALUES ('lutecetools.site_property.globalPom.version', '3.0.3');
INSERT INTO core_datastore(entity_key, entity_value) VALUES ('lutecetools.site_property.deprecatedComponents.textblock', '');
INSERT INTO core_datastore(entity_key, entity_value) VALUES ('lutecetools.site_property.prettify.skin','desert');

DROP TABLE IF EXISTS tools_jenkins_build_result;
CREATE TABLE tools_jenkins_build_result (
	artifact_name	varchar(100) NOT NULL,
	build_result	varchar(100) NOT NULL,
	PRIMARY KEY (artifact_name)
);

