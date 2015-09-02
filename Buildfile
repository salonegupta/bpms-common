#
#  Copyright (C) 2009, Intalio Inc.
#
#  The program(s) herein may be used and/or copied only with the
#  written permission of Intalio Inc. or in accordance with the terms
#  and conditions stipulated in the agreement/contract under which the
#  program(s) have been supplied.

require "install.rb"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "7.5.1-SNAPSHOT"

define "bpms" do
  puts "Defining #{VERSION_NUMBER}"
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.common"

  compile.options.source = "1.5"
  compile.options.target = "1.5"

  define "common" do
    libs = AXIS2.values, APACHE_COMMONS[:lang], APACHE_COMMONS[:httpclient], INTALIO_STATS,JSON_NAGGIT,JSP_API, LOG4J,
    SERVLET_API, SLF4J[:api], SLF4J[:log4j12], SLF4J[:jcl104overslf4j], SPRING[:core],SPRING[:webmvc], TOMCAT_CATALINA,
    VELOCITY

    compile.with libs
    package(:jar)
  end

  define "utils" do
    libs = LOG4J, SERVLET_API_30
    compile.with libs
    package(:jar)
  end

end
