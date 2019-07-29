package omar.video

import io.swagger.annotations.*
import omar.core.HttpStatusMessage
import omar.core.HttpStatus
import javax.imageio.ImageIO

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
	def videoStreamingService

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
			required = false),
		@ApiImplicitParam( name = 'buildThumbnails', 
		                   value = 'Build thumbnails', 
								 allowableValues="true,false", 
								 defaultValue="true", 
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

	@ApiOperation( value = "Create a thumbnail given the id", 
		produces = 'text/plain', 
		httpMethod = 'POST' )
	@ApiImplicitParams( [
		@ApiImplicitParam( 
			name = 'id', 
			value = 'indexId or a record id', 
			dataType = 'string', 
			paramType = "query",
			required = true ),
		@ApiImplicitParam( name = 'size', 
		                   value = 'Thumbnail size', 
								 allowableValues="256,128,64,32", 
								 defaultValue="256", 
								 dataType = "integer", 
								 paramType = "query", 
								 required = false),
		@ApiImplicitParam( name = 'type', 
		                   value = 'Thumbnail type', 
								 allowableValues="jpeg,png", 
								 defaultValue="jpeg", 
								 dataType = "string", 
								 paramType = "query", 
								 required = false)
	] )
	def getThumbnail(GetThumbnailCommand command)
	{	
      def outputStream = null
		try
		{
			HashMap result = videoStreamingService.getOrCreateThumbnail(command)
			if(result)
			{
				String type = command.type?:"png"
				response.contentType = "image/${type}"
				outputStream = response.outputStream
				response.status = 200
				if(result.buffer?.length) response.contentLength = result.buffer.length
				response.setDateHeader('Expires', System.currentTimeMillis() + 60*60*1000)
				if(outputStream&&result.buffer)
				{
					outputStream << result.buffer
				}
			}
			else
			{
				response.contentType = "text/plain"
				response.status = HttpStatus.INTERNAL_SERVER_ERROR
				outputStream << "Error: Unable to produce thumbnail for id ${command?.id}!\n"
			}	
		}
		catch(e)
		{
			response.status = HttpStatus.INTERNAL_SERVER_ERROR
			log.error(e.message)
		}
		finally
		{
			if(outputStream!=null)
			{
				try{
					outputStream.close()
				}
				catch(e)
				{
					log.debug(e.message)
				}
			}
		}

	}

}
