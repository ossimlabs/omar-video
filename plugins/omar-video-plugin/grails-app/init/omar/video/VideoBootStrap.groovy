package omar.video

import groovy.sql.Sql
import groovy.util.logging.Slf4j

import java.sql.SQLException

@Slf4j
class VideoBootStrap {

    def dataSource
    def grailsApplication

    def init = { servletContext ->
        def sql = new Sql(dataSource)
        try {
            sql?.executeUpdate "create index if not exists video_data_set_ground_geom_idx on video_data_set using gist ( ground_geom )"
        }
        catch (final SQLException e) {
            log.error(e.message + "; Bootstrap init failure. If unable to create video_data_set, " +
                    "the likely cause is missing postgis extensions/schemas. Omar Video plugin " +
                    "requires postgis DB schema.")
            throw (e)
        }
        finally {
            sql?.close()
        }
    }
    def destroy = {
    }
}
