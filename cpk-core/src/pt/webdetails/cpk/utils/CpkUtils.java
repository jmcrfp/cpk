/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpk.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class CpkUtils {
  protected static Log logger = LogFactory.getLog( CpkUtils.class );

  public static void setResponseHeaders( HttpServletResponse response, final String mimeType ) {
    setResponseHeaders( response, mimeType, 0, null, 0 );
  }

  public static void setResponseHeaders( HttpServletResponse response, final String mimeType,
                                         final String attachmentName ) {
    setResponseHeaders( response, mimeType, 0, attachmentName, 0 );
  }

  public static void setResponseHeaders( HttpServletResponse response, final String mimeType,
                                         final String attachmentName,
                                         long attachmentSize ) {
    setResponseHeaders( response, mimeType, 0, attachmentName, attachmentSize );

  }

  public static void setResponseHeaders( HttpServletResponse response, final String mimeType, final int cacheDuration,
                                         final String attachmentName, long attachmentSize ) {


    if ( response == null ) {
      logger.warn( "Parameter 'httpresponse' not found!" );
      return;
    }

    if ( mimeType != null ) {
      response.setHeader( "Content-Type", mimeType );
    }

    if ( attachmentName != null ) {
      response.setHeader( "content-disposition", "attachment; filename=" + attachmentName );
    } // Cache?

    if ( attachmentSize > 0 ) {
      response.setHeader( "Content-Length", String.valueOf( attachmentSize ) );
    }

    if ( cacheDuration > 0 ) {
      response.setHeader( "Cache-Control", "max-age=" + cacheDuration );
    } else {
      response.setHeader( "Cache-Control", "max-age=0, no-store" );
    }
  }

  public static void redirect( HttpServletResponse response, String url ) {

    if ( response == null ) {
      logger.error( "response not found" );
      return;
    }
    try {
      response.sendRedirect( url );
    } catch ( IOException e ) {
      logger.error( "could not redirect", e );
    }
  }


  public static Map<String, Object> getRequestParameters(
    Map<String, Map<String, Object>> bloatedMap ) {
    return bloatedMap.get( "request" );
  }


  public static Map<String, Object> getPathParameters( Map<String, Map<String, Object>> bloatedMa ) {
    return bloatedMa.get( "path" );
  }

  public static OutputStream getResponseOutputStream( HttpServletResponse response ) throws IOException {
    return response.getOutputStream();
  }

  public static void send( HttpServletResponse response, InputStream fileInputStream, String mimeTypes, String fileName, boolean sendAsAttachment ) {
    Integer contentLength = null;
    try {
      contentLength = fileInputStream.available();
    } catch ( IOException e ) {
      logger.error( "Failed setting attachment size.", e );
    }

    send( response, fileInputStream, mimeTypes, fileName, sendAsAttachment, contentLength );
  }

  public static void send( HttpServletResponse response, InputStream fileInputStream, String mimeTypes,
                     String fileName, boolean sendAsAttachment, Integer contentLength ) {
    if ( mimeTypes != null && !mimeTypes.isEmpty()) {
      response.setContentType( mimeTypes );
    }

    String disposition = sendAsAttachment ? "attachment" : "inline";
    String fileParam = fileName != null && !fileName.isEmpty() ? "; filename=" + fileName : "";
    response.setHeader( "Content-disposition", disposition + fileParam );

    if ( contentLength != null ) {
      response.setContentLength( contentLength );
    }

    try {
      IOUtils.copy( fileInputStream, response.getOutputStream() );
      response.getOutputStream().flush();
      fileInputStream.close();
    } catch ( Exception ex ) {
      logger.error( "Failed to copy file to outputstream: " + ex );
    }
  }

}




