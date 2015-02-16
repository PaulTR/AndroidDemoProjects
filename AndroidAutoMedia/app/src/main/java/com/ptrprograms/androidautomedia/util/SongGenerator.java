package com.ptrprograms.androidautomedia.util;

import com.ptrprograms.androidautomedia.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulruiz on 2/13/15.
 */
public class SongGenerator {

    public static List<Song> generateSongs() {
        List<Song> songs = new ArrayList<>();

        songs.add( new Song( "blueingreenmilesdavis",
                "Blue in Green",
                "Miles Davis",
                "Kind of Blue",
                "Jazz",
                "http://upload.wikimedia.org/wikipedia/en/9/9c/MilesDavisKindofBlue.jpg",
                "http://upload.wikimedia.org/wikipedia/en/9/9c/MilesDavisKindofBlue.jpg" ) );
        songs.add( new Song( "takefivedavebrubeck",
                "Take Five",
                "Dave Brubeck",
                "Time Out",
                "Jazz",
                "http://upload.wikimedia.org/wikipedia/en/e/e5/Time_out_album_cover.jpg",
                "http://upload.wikimedia.org/wikipedia/en/e/e5/Time_out_album_cover.jpg" ) );
        songs.add( new Song( "lonelywomanhoracesilver",
                "Lonely Woman",
                "Horace Silver",
                "Song For My Father",
                "Jazz",
                "http://upload.wikimedia.org/wikipedia/en/0/03/Song_for_My_Father_%28Horace_Silver_album_-_cover_art%29.jpg",
                "http://upload.wikimedia.org/wikipedia/en/0/03/Song_for_My_Father_%28Horace_Silver_album_-_cover_art%29.jpg" ) );

        songs.add( new Song( "boscostompsavoydoucet",
                "Bosco Stomp",
                "Savoy-Doucet Cajun Band",
                "With Spirits",
                "Cajun",
                "http://i.ebayimg.com/04/!B0r3h4wB2k~$(KGrHqR,!hwE)qgK8E2dBMb(Re1SRw~~_35.JPG",
                "http://i.ebayimg.com/04/!B0r3h4wB2k~$(KGrHqR,!hwE)qgK8E2dBMb(Re1SRw~~_35.JPG" ) );

        songs.add( new Song( "cherebebecreolerayabshire",
                "Chere Bebe Creole",
                "Ray Abshire",
                "Pour Les Bons Vieux Temps",
                "Cajun",
                "http://www.acousticmusic.com/fame/g02464.jpg",
                "http://www.acousticmusic.com/fame/g02464.jpg" ) );

        songs.add( new Song( "bayoupompomwallaceread",
                "Bayou Pom Pom",
                "Wallace Read",
                "Cajun House Party",
                "Cajun",
                "http://i43.tower.com/images/mm106419031/cajun-house-party-cez-cheese-wallace-read-cd-cover-art.jpg",
                "http://i43.tower.com/images/mm106419031/cajun-house-party-cez-cheese-wallace-read-cd-cover-art.jpg" ) );

        songs.add( new Song( "battleforevermoreledzeppelin",
                "Battle for Evermore",
                "Led Zeppelin",
                "Led Zeppelin IV",
                "Rock",
                "http://img3.wikia.nocookie.net/__cb20110330231709/lotr/images/7/77/LedZeppelinFourSymbols.jpg",
                "http://img3.wikia.nocookie.net/__cb20110330231709/lotr/images/7/77/LedZeppelinFourSymbols.jpg" ) );

        songs.add( new Song( "rockandrollbandboston",
                "Rock and Roll Band",
                "Boston",
                "Boston",
                "Rock",
                "http://upload.wikimedia.org/wikipedia/en/2/23/BostonBoston.jpg",
                "http://upload.wikimedia.org/wikipedia/en/2/23/BostonBoston.jpg" ) );

        songs.add( new Song( "veteranofthepsychicwarsblueoystercult",
                "Veteran of the Psychic Wars",
                "Blue Oyster Cult",
                "Fire of Unknown Origin",
                "Rock",
                "http://upload.wikimedia.org/wikipedia/en/5/5f/Fire_Of_Unknown_origin.jpg",
                "http://upload.wikimedia.org/wikipedia/en/5/5f/Fire_Of_Unknown_origin.jpg" ) );

        return songs;
    }
}
