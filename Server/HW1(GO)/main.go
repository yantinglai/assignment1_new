package main

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

type ImageMetaData struct {
	AlbumID   string `json:"albumID"`
	ImageSize string `json:"imageSize"`
}

type AlbumInfo struct {
	Artist string `json:"artist"`
	Title  string `json:"title"`
	Year   string `json:"year"`
}

type ErrorMsg struct {
	ErrorMsg string `json:"errorMsg"`
}

func main() {
	r := gin.Default()
	r.POST("/albums/*albumID", postHandler)
	r.GET("/albums/*albumID", getHandler)
	r.Run(":8081")  // Default listens on :8080
}

func postHandler(c *gin.Context) {
    defer func() {
        if r := recover(); r != nil {
            // Handle the panic here and return an error response
            errorMessage := ErrorMsg{ErrorMsg: "Internal server error"}
            c.JSON(http.StatusInternalServerError, errorMessage)
        }
    }()

    size := c.Request.ContentLength
    if size < 0 {
        panic("Invalid content length") // Simulate a panic for demonstration
    }

    imageData := ImageMetaData{
        AlbumID:   "123",
        ImageSize: string(size),
    }
    c.JSON(http.StatusOK, imageData)
}


func getHandler(c *gin.Context) {
    albumID := c.Param("albumID")
    if albumID == "" {
        errorMessage := ErrorMsg{ErrorMsg: "You need to specify album id"}
        c.JSON(http.StatusNotFound, errorMessage)
        return
    }
    album := AlbumInfo{
        Artist: "Sex Pistols",
        Title:  "Never Mind The Bollocks!",
        Year:   "1977",
    }
    c.JSON(http.StatusOK, album)
}
