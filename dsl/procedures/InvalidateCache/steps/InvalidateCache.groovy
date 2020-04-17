$[/myProject/groovy/scripts/preamble.groovy.ignore]

AWSCloudFront plugin = new AWSCloudFront()
plugin.runStep('Invalidate Cache', 'Invalidate Cache', 'invalidateCache')