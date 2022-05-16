package com.dorianmercier.mediamanager.background;

import com.dorianmercier.mediamanager.Database.Media;

import java.util.Comparator;

public class MediaComparator implements Comparator<Media> {
    @Override
    public int compare(Media m2, Media m1) {
        if(m1.year != m2.year) return m1.year - m2.year;
        else {
            if(m1.month != m2.month) return m1.month - m2.month;
            else {
                if(m1.day != m2.day) return m1.day - m2.day;
                else {
                    if(m1.hour != m2.hour) return m1.hour - m2.hour;
                    else {
                        if(m1.minute != m2.minute) return m1.minute - m2.minute;
                        else return m1.second - m2.second;
                    }
                }
            }
        }
    }
}
