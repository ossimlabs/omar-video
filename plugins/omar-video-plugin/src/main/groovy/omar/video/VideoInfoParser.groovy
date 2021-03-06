package omar.video

import groovy.util.slurpersupport.GPathResult

import omar.core.Repository
import omar.stager.core.OmsInfoParser
import omar.video.VideoDataSet

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: May 13, 2010
 * Time: 6:01:33 PM
 * To change this template use File | Settings | File Templates.
 */
class VideoInfoParser implements OmsInfoParser
{
  def additionalTags
  //def tagFile = new File("tags.txt")

  public def processDataSets( GPathResult oms, Repository repository = null )
  {
    def videoDataSets = []

    for ( def videoDataSetNode in oms?.dataSets?.VideoDataSet )
    {

      VideoDataSet videoDataSet = VideoDataSet.initVideoDataSet( videoDataSetNode )

      videoDataSet.repository = repository
      videoDataSets << videoDataSet
      //repository?.addToVideoDataSets(videoDataSet)
    }

    return videoDataSets
  }
}
