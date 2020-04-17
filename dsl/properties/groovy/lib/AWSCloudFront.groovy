import com.amazonaws.services.cloudfront.model.Invalidation
import com.cloudbees.flowpdf.*

/**
 * AWSCloudFront
 */
class AWSCloudFront extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
            pluginName         : '@PLUGIN_KEY@',
            pluginVersion      : '@PLUGIN_VERSION@',
            configFields       : ['config'],
            configLocations    : ['ec_plugin_cfgs'],
            defaultConfigValues: [:]
        ]
    }

    @Lazy
    CloudFrontWrapper wrapper = {
        def credential = context.getConfigValues().getRequiredCredential('credential')
        return new CloudFrontWrapper(credential.userName, credential.secretValue)
    }()


    /**
     * invalidateCache - Invalidate Cache/Invalidate Cache
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param distributionId (required: true)
     * @param objectPaths (required: true)
     * @param callerReference (required: false)
     * @param uniqueCallerReference (required: false)

     */
    def invalidateCache(StepParameters p, StepResult sr) {
        String distributionId = p.getRequiredParameter('distributionId')?.value
        String objectPaths = p.getRequiredParameter('objectPaths')?.value
        String callerReference = p.asMap.callerReference ?: ''

        if (p.asMap.uniqueCallerReference == 'true') {
            callerReference = "Auto Generated Reference at " + new Date()
        }
        List paths = objectPaths.split(/\n+/).collect { it.trim() }

        Invalidation invalidation
        try {
            invalidation = wrapper.invalidateCache(distributionId, paths, callerReference)
        } catch (Throwable e) {
            context.bailOut("Failed to invalidate: ${e.message}")
        }

        log.info "Invalidation ID: ${invalidation.id}"
        log.info "Invalidation Status: ${invalidation.status}"

        while ('Completed' != invalidation.status) {
            sleep(1000 * 5)
            invalidation = wrapper.getInvalidation(distributionId, invalidation.id)
            log.info "Waiting for invalidation to complete (status: ${invalidation.status})...."
        }
    }

    // === step ends ===

}