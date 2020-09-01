package omar.video

import groovy.sql.Sql

class VideoBootStrap {

    def dataSource
    def grailsApplication

    def init = { servletContext ->
      def sql = new Sql(dataSource)
      
      sql?.executeUpdate "create index if not exists raster_entry_ground_geom_idx on raster_entry using gist ( ground_geom )"
      sql?.close()

    }
    def destroy = {
    }
}
