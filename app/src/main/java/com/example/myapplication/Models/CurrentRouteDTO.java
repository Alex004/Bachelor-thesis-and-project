package com.example.myapplication.Models;

import java.io.Serializable;
import java.util.List;


// This class is used for sending calculate route (a list of codes of some points) for knowing
// the next important point where selected object are find or important points with intermidiar points
public class CurrentRouteDTO implements Serializable {
    private List<String> currentRoute;
    public CurrentRouteDTO(List<String> currentRoute)
    {
        this.currentRoute = currentRoute;
    }
    public CurrentRouteDTO()
    {

    }

    public List<String> getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(List<String> currentRoute) {
        this.currentRoute = currentRoute;
    }
}
