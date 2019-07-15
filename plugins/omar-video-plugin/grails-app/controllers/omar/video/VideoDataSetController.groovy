package omar.video

import io.swagger.annotations.*
import omar.core.HttpStatusMessage

@Api( value = "dataManager",
		description = "DataManager Support"
)
class VideoDataSetController
{
	static allowedMethods = [
		addVideo: 'POST',
		removeVideo: 'POST'
	]

	def videoDataSetService

	@ApiOperation( value = "Add a Video to the database", 
		produces = 'text/plain', 
		httpMethod = 'POST' )
	@ApiImplicitParams( [
		@ApiImplicitParam( 
			name = 'filename', 
			value = 'Path to file to add', 
			dataType = 'string', 
			paramType = "query",
			required = true ),
		@ApiImplicitParam( 
			name = 'convert', 
			value = 'Convert to MP4', 
			allowableValues="true,false", 
			defaultValue="false", 
			dataType = "boolean", 
			paramType = "query", 
			required = false)
	] )
	def addVideo()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = videoDataSetService.addVideo( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}

	@ApiOperation( value = "Remove a Video from the database", 
		produces = 'text/plain', 
		httpMethod = 'POST' )
	@ApiImplicitParams( [
		@ApiImplicitParam( name = 'filename', 
							value = 'Path to file to add', 
							dataType = 'string', 
							paramType = "query",
							required = true )
	] )
	def removeVideo()
	{
		def httpStatusMessage = new HttpStatusMessage()
		def status = videoDataSetService.removeVideo( httpStatusMessage, params )

		response.status = httpStatusMessage.status
		render( httpStatusMessage.message )
	}
}
