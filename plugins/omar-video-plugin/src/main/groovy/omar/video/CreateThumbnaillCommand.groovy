package omar.video

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class CreateThumbnailCommand implements Validateable
{
   String inputFile
   String outputFile
   String offset = "00:00:00"
   Integer quality = 2
   
   static constraints = {
      inputFile  nullable: false
      outputFile nullable: false
      offset     nullable: true 
      quality    nullable: true
   }

}