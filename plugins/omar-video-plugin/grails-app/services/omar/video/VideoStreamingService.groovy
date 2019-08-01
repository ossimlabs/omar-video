package omar.video

import grails.transaction.Transactional
import org.apache.commons.io.FilenameUtils
import javax.imageio.ImageIO
import static grails.async.Promises.*
import java.awt.image.BufferedImage

@Transactional( readOnly = true )
class VideoStreamingService
{
	def grailsLinkGenerator
	def grailsApplication

	HashMap getVideoServerLocation()
	{
		HashMap result = [
			videoServerUrlRoot: grailsApplication.config.videoStreaming.videoServerUrlRoot,
		   videoServerDirRoot: grailsApplication.config.videoStreaming.videoServerDirRoot
		]

		if(result.videoServerDirRoot)
		{
			result.thumbnailDir = new File(result.videoServerDirRoot, "thumbnails").toString()
		}
		else
		{
			result.thumbnailDir = ""
		}

		result
	}

	static def getVideoFilename(String id)
	{
		String result
		if(id)
		{
			def videoId = (id ==~ /\d+/ ) ? id as Long : null

			def videoDataSet = VideoDataSet.where {
				id == videoId || indexId == id
			}.get()

			result = videoDataSet?.filename
		}

		result
	}

	static String getThumbnailNameFromVideoFile(String videoFile)
	{
		String result

		if(videoFile)
		{
			videoFile.take(videoFile.lastIndexOf('.'))

			result = "${videoFile}-thumb.jpg"
		}
	}

	def createThumbnail(String source)
	{
		String result
		HashMap videoServerLocation = getVideoServerLocation()
		File tempFile = source as File
		String thumbnailFilename = tempFile.name
		thumbnailFilename = getThumbnailNameFromVideoFile(thumbnailFilename)
		File thumbnailDir = videoServerLocation.thumbnailDir as File
		File outputThumbnailFilename = new File( thumbnailDir, 
																thumbnailFilename )
		if(!thumbnailDir.exists())
		{
			thumbnailDir.mkdirs()
		}

		// create if not exists
		if(!outputThumbnailFilename.exists())
		{
			generateThumbnail(new CreateThumbnailCommand(   
				inputFile :  source,
				outputFile : outputThumbnailFilename,
				offset :     "00:00:00",
				quality : 2))
		}
		result = outputThumbnailFilename.toString()

		result
	}

	def getOrCreateThumbnail(GetThumbnailCommand command)
	{
		String videoFilename = getVideoFilename(command.id)
		HashMap videoServerLocation = getVideoServerLocation()
		HashMap result = [:]
		if(videoFilename)
		{
			String outputThumbnail = createThumbnail(videoFilename)
			//scale to the requested size
			if(outputThumbnail)
			{
				File outputThumbnailFilename = outputThumbnail as File
				BufferedImage img = ThumbnailUtilities.fileToBufferedImage(outputThumbnailFilename)
				Integer size = command.size?:128
				BufferedImage thumbnailImage = ThumbnailUtilities.createThumbnail(img, size, command.type?:"jpeg");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(thumbnailImage, command.type, baos);
				result.buffer = baos.buf
			}
		}

		result
	}

	def getVideoDetails( def params )
	{
		HashMap videoServerLocation = getVideoServerLocation()
		def videoURL = null
		String videoFilename = getVideoFilename(params.id)
		log.debug videoDataSet.filename

		if ( videoFilename )
		{
			def videoFile = videoFilename as File
			def mp4File = new File( videoServerLocation.videoServerDirRoot, "${FilenameUtils.getBaseName( videoFile.name ) }.mp4" )

			videoURL = grailsLinkGenerator.link( absolute: true, base: videoServerLocation.videoServerUrlRoot, uri: "/${ mp4File.name }" )

			if ( !mp4File.exists() )
			{
				def p = task {
					convertVideo( videoFile, mp4File )
				}

				waitAll( p )
			}
		}	

		[ videoDataSet: videoDataSet, videoURL: videoURL ]
	}

	private static def generateThumbnail(CreateThumbnailCommand command)
	{
		def cmd = [
			'ffmpeg',
			"-i",
			command.inputFile,
			"-ss",
			command.offset,
			"-vframes",
			"1",
			"-q:v",
			command.quality,
			command.outputFile
		]

		executeCommand(cmd)
	}
	private static def convertVideo( File inputFile, File outputFile )
	{
		def cmd = [
			'ffmpeg',
			'-i', inputFile.absolutePath,
			'-deinterlace',
			'-pix_fmt', 'yuv420p',
			'-vcodec', 'libx264', 
			'-preset', 'slow', 
			'-vprofile', 'high', 
			'-trellis', '2', 
			'-crf', '20', 
			'-acodec', 'libfaac',
			'-ac', '2', 
			'-ab', '192k',
			'-f', 'mp4', 
			'-y', 
			outputFile.absolutePath
		]
		executeCommand(cmd)
	}
	private static def executeCommand(def cmd)
	{
		println cmd.join( ' ' )

		def start = System.currentTimeMillis()
		def proc = cmd.execute()

		proc.consumeProcessOutput()

		def exitCode = proc.waitFor()
		def stop = System.currentTimeMillis()

		println "elapsed: ${ stop - start }ms"
		println "exitCode: ${ exitCode }"

	}

}
