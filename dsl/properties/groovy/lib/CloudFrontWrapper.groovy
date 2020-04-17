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

class CloudFrontWrapper {
    AmazonCloudFront client

    CloudFrontWrapper(String username, String password) {

        def credential = new BasicAWSCredentials(username, password)
        AWSCredentialsProvider credentialProvider = new AWSStaticCredentialsProvider(credential)
        client = AmazonCloudFrontClientBuilder
            .standard()
            .withRegion(Regions.DEFAULT_REGION)
            .withCredentials(credentialProvider)
            .build()
    }


    Invalidation invalidateCache(String distributionId, List paths, String callerReference) {
        Paths p = new Paths().withItems(paths).withQuantity(paths.size())
        InvalidationBatch batch = new InvalidationBatch().withPaths(p).withCallerReference(callerReference)
        CreateInvalidationResult result = client.createInvalidation(
            new CreateInvalidationRequest()
                .withDistributionId(distributionId)
                .withInvalidationBatch(batch)
        )

        Invalidation invalidation = result.invalidation
        return invalidation
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
