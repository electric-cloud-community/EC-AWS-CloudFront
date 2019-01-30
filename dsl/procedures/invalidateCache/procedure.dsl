import java.io.File

def procName = 'Invalidate Cache'
procedure procName, description: 'Invalidating objects removes them from CloudFront edge caches. A faster and less expensive method is to use versioned object or directory names. For more information, see Invalidating Objects in the Amazon CloudFront Developer Guide.', {

	step 'Setup', {
		subprocedure = 'Setup'
	}

	step 'Invalidate Cache',
	  command: new File(pluginDir, "dsl/procedures/invalidateCache/steps/invalidate.groovy").text,
	  shell: 'ec-groovy'
}

