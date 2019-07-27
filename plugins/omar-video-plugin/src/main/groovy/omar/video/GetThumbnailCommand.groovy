package omar.video

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class GetThumbnailCommand implements Validateable
{
   String id
   Integer size=128
   String type="jpeg"
   static constraints = {
      id nullable: false
      size nullable: true
      type nullable: true
   }

}