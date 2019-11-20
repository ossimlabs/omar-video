package omar.video

import groovy.sql.Sql

class VideoBootStrap {

    def dataSource
    def grailsApplication

    def init = { servletContext ->

      // def domainClass = grailsApplication.getDomainClass('omar.video.VideoDataSet')
      // def property = domainClass?.getPersistentProperty('fileType')

      // if (!property) {
      //   def sql = new Sql(dataSource)

      //   sql.executeUpdate(
      //     "ALTER TABLE video_data_set ADD COLUMN file_type VARCHAR;"
      //     );

      //   sql?.close()
      // }

    }
    def destroy = {
    }
}
