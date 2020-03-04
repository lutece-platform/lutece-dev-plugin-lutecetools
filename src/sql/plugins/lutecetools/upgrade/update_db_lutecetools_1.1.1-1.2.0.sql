DROP TABLE IF EXISTS tools_jenkins_build_result;
CREATE TABLE tools_jenkins_build_result (
	artifact_name	varchar(100) NOT NULL,
	build_result	varchar(100) NOT NULL,
	PRIMARY KEY (artifact_name)
);

