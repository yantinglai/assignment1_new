import com.google.gson.Gson;
import io.swagger.client.model.AlbumInfo;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@WebServlet(name = "AlbumStoreServlet", value = "/albums/*")
public class AlbumStoreServlet extends HttpServlet {

    private static final String BASE_API_URL = "https://virtserver.swaggerhub.com/IGORTON/AlbumStore/1.0.0";
    private Gson gson = new Gson();
    private static final int BYTE_SIZE = 8192;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        InputStream inputStream = req.getInputStream();
        int size = 0;
        byte[] buffer = new byte[BYTE_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            size += bytesRead;
        }

        res.setStatus(HttpServletResponse.SC_OK);
        imageMetaData imageData = new imageMetaData("123", String.valueOf(size));
        String str = this.gson.toJson(imageData);
        PrintWriter out = res.getWriter();
        out.print(str);
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();

        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isGetUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorMsg getErrorMsgGet = new errorMsg("You need to specify album id");
            String errorString = this.gson.toJson(getErrorMsgGet);
            PrintWriter out = res.getWriter();
            out.print(errorString);
            out.flush();
        } else {
            AlbumInfo album = getAlbumByKey(urlParts[1]);
            String albumString = this.gson.toJson(album);
            res.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = res.getWriter();
            out.print(albumString);
            out.flush();
        }
    }

    private boolean isGetUrlValid(String[] urlPath) {
        return urlPath.length == 2 && !urlPath[1].isEmpty();
    }

    private AlbumInfo getAlbumByKey(String albumID) throws IOException {
        URL url = new URL(BASE_API_URL + "/albums/" + albumID);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed to connect: HTTP error code : " + connection.getResponseCode());
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = br.lines().collect(Collectors.joining());
            return gson.fromJson(response, AlbumInfo.class);
        } finally {
            connection.disconnect();
        }
    }

    class imageMetaData {
        private String albumID;
        private String imageSize;

        imageMetaData(String albumID, String imageSize) {
            this.albumID = albumID;
            this.imageSize = imageSize;
        }
    }

    class errorMsg {
        private String errorMsg;

        errorMsg(String msg) {
            this.errorMsg = msg;
        }
    }

}
