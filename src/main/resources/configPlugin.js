/**
 * Confluence SAML Plugin - a confluence plugin to allow SAML 2.0
 *  authentication. 
 *
 *  Copyright (C) 2014 Bitium, Inc.
 *  
 *  This file is part of Confluence SAML Plugin.
 *  
 *  Confluence SAML Plugin is free software: you can redistribute it 
 *  and/or modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation, either version 3 of
 *  the License, or (at your option) any later version.
 *  
 *  Confluence SAML Plugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 */
var client;
setTimeout(function() {
        client = new ZeroClipboard( document.getElementById("copy-button") );

        client.on( "copy", function (event) {
          var clipboard = event.clipboardData;
	  var samlField = document.getElementById("samlEndpoint");
          clipboard.setData( "text/plain", samlField.value );
          var endpointCopied = document.getElementById("endpoint-copied");
          endpointCopied.style.visibility = "visible";
	  setTimeout(function() {
            endpointCopied.style.visibility = "hidden";
          }, 2000);
	  return false;
        });
}, 500);
