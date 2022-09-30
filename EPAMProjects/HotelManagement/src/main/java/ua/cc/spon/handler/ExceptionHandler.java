package ua.cc.spon.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@WebServlet({"/errorAction"})
public class ExceptionHandler extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Analyze the servlet exception
        Throwable throwable
                = (Throwable) request.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");

        if (servletName == null) {
            servletName = "Unknown";
        }
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

        if (requestUri == null) {
            requestUri = "Unknown";
        }

        // Set response content type
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html lang=\"en\">");
        out.println("  <head>");
        out.println("    <meta charset=\"utf-8\"/>");
        out.println("   <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, viewport-fit=cover\"/>");
        out.println("   <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\"/>");
        out.println("   <title>Page 500 - Hotel</title>");
        out.println("   <!-- CSS files -->");
        out.println("   <link href=\"./dist/css/tabler.min.css\" rel=\"stylesheet\"/>");
        out.println("   <link href=\"./dist/css/tabler-flags.min.css\" rel=\"stylesheet\"/>");
        out.println("   <link href=\"./dist/css/tabler-payments.min.css\" rel=\"stylesheet\"/>");
        out.println("   <link href=\"./dist/css/tabler-vendors.min.css\" rel=\"stylesheet\"/>");
        out.println("   <link href=\"./dist/css/demo.min.css\" rel=\"stylesheet\"/>");
        out.println(" </head>");
        out.println(" <body  class=\" border-top-wide border-primary d-flex flex-column\">");
        out.println("   <div class=\"page page-center\">");
        out.println("     <div class=\"container-tight py-4\">");
        out.println("       <div class=\"empty\">");
        out.println("         <div class=\"empty-header\">" + ((statusCode == null) ? "500" : statusCode) + "</div>");
        out.println("         <p class=\"empty-title\">Oops… You just found an error page</p>");
        out.println("         <p class=\"empty-subtitle text-muted\">");
        out.println("               We are sorry but our server encountered an internal error");
        out.println("               </p>");
        out.println("         <div class=\"empty-action\">");
        out.println("           <a href=\"./.\" class=\"btn btn-primary\">");
        out.println("             <!-- Download SVG icon from http://tabler-icons.io/i/arrow-left -->");
        out.println("             <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><path stroke=\"none\" d=\"M0 0h24v24H0z\" fill=\"none\"/><line x1=\"5\" y1=\"12\" x2=\"19\" y2=\"12\" /><line x1=\"5\" y1=\"12\" x2=\"11\" y2=\"18\" /><line x1=\"5\" y1=\"12\" x2=\"11\" y2=\"6\" /></svg>");
        out.println("               Take me home");
        out.println("           </a>");
        out.println("         </div>");
        out.println("       </div>");
        out.println("     </div>");
        out.println("   </div>");
        out.println("   <!-- Libs JS -->");
        out.println("   <!-- Tabler Core -->");
        out.println("   <script src=\"./dist/js/tabler.min.js\" defer></script>");
        out.println("   <script src=\"./dist/js/demo.min.js\" defer></script>");
        out.println(" </body>");
        out.println("</html>");

        LOGGER.error("{} - {} - {} - {}", throwable.getMessage(), servletName, requestUri, Arrays.toString(throwable.getStackTrace()));

    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}