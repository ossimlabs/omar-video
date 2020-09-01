package omar.video

import groovy.sql.Sql

class VideoBootStrap {

    def dataSource
    def grailsApplication

    def init = { servletContext ->
      def sql = new Sql(dataSource)

      sql?.executeUpdate "create index if not exists video_data_set_ground_geom_idx on video_data_set using gist ( ground_geom )"
      sql?.close()

    }
    def destroy = {
    }
}
