#
#  Copyright (C) 2009, Intalio Inc.
#
#  The program(s) herein may be used and/or copied only with the
#  written permission of Intalio Inc. or in accordance with the terms
#  and conditions stipulated in the agreement/contract under which the
#  program(s) have been supplied.

repositories.remote = [ "http://release.intalio.com/m2repo", "http://www.intalio.org/public/maven2", "http://mirrors.ibiblio.org/pub/mirrors/maven2" ]

repositories.release_to[:username] ||= "release"
repositories.release_to[:url] ||= "sftp://www.intalio.org/var/www-org/public/maven2"
repositories.release_to[:permissions] ||= 0664
