
$[/myProject/scripts/preamble]

import com.electriccloud.client.groovy.ElectricFlow

def configuration = new EFPlugin().getConfiguration('$[config]')
String accessKey = configuration.userName
String secret = configuration.password

ElectricFlow ef = new ElectricFlow()

CloudFrontPlugin plugin = new CloudFrontPlugin(accessKey, secret)
String objectPaths = ef.getProperty(propertyName: 'objectPaths')?.property?.value
String distributionId = ef.getProperty(propertyName: 'distributionId')?.property?.value
String callerReference = ef.getProperty(propertyName: 'callerReference')?.property?.value
Boolean uniqueCallerReference = ef.getProperty(propertyName: 'uniqueCallerReference')?.property?.value == 'true'
if (uniqueCallerReference) {
    callerReference = "Auto Generated Reference at " + new Date()
}

List paths = objectPaths.split(/\n+/).collect { it }

try {
    plugin.invalidateCache(distributionId, paths, callerReference)
} catch (Throwable e) {
    ef.setProperty(propertyName: '/myJobStep/summary', e.message)
    System.exit(-1)
}


