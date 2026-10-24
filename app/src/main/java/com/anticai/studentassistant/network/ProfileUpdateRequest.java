
package com.anticai.studentassistant.network;

public class ProfileUpdateRequest {

    private String name;
    private String college;
    private String course;
    private String year;

    public ProfileUpdateRequest(
            String name,
            String college,
            String course,
            String year
    ) {
        this.name = name;
        this.college = college;
        this.course = course;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public String getCollege() {
        return college;
    }

    public String getCourse() {
        return course;
    }

    public String getYear() {
        return year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
