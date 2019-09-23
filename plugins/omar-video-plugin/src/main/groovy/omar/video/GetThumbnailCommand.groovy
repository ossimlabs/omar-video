package omar.video

import grails.validation.Validateable
import groovy.transform.ToString

@ToString( includeNames = true )
class GetThumbnailCommand implements Validateable
{
   String id
   Integer w=128
   Integer h=128
   String type="jpeg"
   static constraints = {
      id nullable: false
      w nullable: true
      h nullable: true
      type nullable: true
   }

}