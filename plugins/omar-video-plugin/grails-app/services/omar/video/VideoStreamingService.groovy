package omar.video

import grails.transaction.Transactional
import org.apache.commons.io.FilenameUtils

@Transactional( readOnly = true )
class VideoStreamingService
{
	def grailsLinkGenerator
	def grailsApplication

	def getVideoDetails( def params )
	{
		def videoServerUrlRoot = grailsApplication.config.videoStreaming.videoServerUrlRoot
		def videoServerDirRoot = grailsApplication.config.videoStreaming.videoServerDirRoot
		def videoURL = null

		def videoId = (params.id ==~ /\d+/ ) ? params.id as Long : null

		def videoDataSet = VideoDataSet.where {
			id == videoId || indexId == params?.id
		}.get()

		println videoDataSet.filename

		if ( videoDataSet )
		{
			def videoFile = videoDataSet.filename as File
			def mpgFile = new File( videoServerDirRoot, "${FilenameUtils.getBaseName( videoFile.name ) }.mpg" )

			videoURL = grailsLinkGenerator.link( absolute: true, base: videoServerUrlRoot, uri: "/${ mpgFile.name }" )

			if ( !mpgFile.exists() )
			{
				convertVideo( videoFile, mpgFile )
			}
		}

		[ videoDataSet: videoDataSet, videoURL: videoURL ]
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
