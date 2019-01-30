@Grapes([
    @Grab('com.amazonaws:aws-java-sdk-cloudfront:1.11.490'),
])
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudfront.AmazonCloudFront
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult
import com.amazonaws.services.cloudfront.model.GetInvalidationRequest
import com.amazonaws.services.cloudfront.model.GetInvalidationResult
import com.amazonaws.services.cloudfront.model.Invalidation
import com.amazonaws.services.cloudfront.model.InvalidationBatch
import com.amazonaws.services.cloudfront.model.Paths

class CloudFrontPlugin {
    AmazonCloudFront client

    CloudFrontPlugin(String username, String password) {

        def credential = new BasicAWSCredentials(username, password)
        AWSCredentialsProvider credentialProvider = new AWSStaticCredentialsProvider(credential)
        client = AmazonCloudFrontClientBuilder
            .standard()
            .withRegion(Regions.DEFAULT_REGION)
            .withCredentials(credentialProvider)
            .build()
    }


    def invalidateCache(String distributionId, List paths, String callerReference) {
        Paths p = new Paths().withItems(paths).withQuantity(paths.size())
        InvalidationBatch batch = new InvalidationBatch().withPaths(p).withCallerReference(callerReference)
        CreateInvalidationResult result = client.createInvalidation(
            new CreateInvalidationRequest()
                .withDistributionId(distributionId)
                .withInvalidationBatch(batch)
        )

        Invalidation invalidation = result.invalidation
        println "Invalidation ID: ${invalidation.id}"
        println "Invalidation Status: ${invalidation.status}"
        if ('Completed' != invalidation.status) {
            print "Waiting for the invalidation to complete"
        }
        while('Completed' != invalidation.status) {
            sleep(1000 * 5)
            invalidation = getInvalidation(distributionId, invalidation.id)
            print "."
        }
    }

    Invalidation getInvalidation(String distributionId, String invalidationId) {
        GetInvalidationResult result = client.getInvalidation(
            new GetInvalidationRequest()
                .withDistributionId(distributionId)
                .withId(invalidationId)
            )
        return result.invalidation
    }
}
