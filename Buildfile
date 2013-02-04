#
#  Copyright (C) 2009, Intalio Inc.
#
#  The program(s) herein may be used and/or copied only with the
#  written permission of Intalio Inc. or in accordance with the terms
#  and conditions stipulated in the agreement/contract under which the
#  program(s) have been supplied.

require File.join(File.dirname(__FILE__), "repositories.rb")

DP_VERSION_NUMBER="1.0.0"
DEPENDENCIES = "#{ENV['HOME']}/.m2/repository/org/intalio/common/dependencies/#{DP_VERSION_NUMBER}/dependencies-#{DP_VERSION_NUMBER}.rb"
unless ENV["M2_REPO"] != ''
  DEPENDENCIES = "#{ENV['M2_REPO']}/org/intalio/common/dependencies/#{DP_VERSION_NUMBER}/dependencies-#{DP_VERSION_NUMBER}.rb"
end
load DEPENDENCIES

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.1.0.004-SNAPSHOT"
 

define "bpms-common" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.common"
  compile.options.source = "1.5"
  compile.options.target = "1.5"
  libs = AXIS2, APACHE_COMMONS[:lang], APACHE_COMMONS[:httpclient], INTALIO_STATS, JSON_NAGGIT, JSP_API, LOG4J, SERVLET_API, SLF4J, SPRING[:core], SPRING[:webmvc]
  compile.from('src').with libs
  package(:jar)

end

