/**
 * applet.js
 *
 * Copyright 2011, Cabo Communications A/S
 * Released under GPL License.
 *
 */

(function($){

   // note: navigator.mimeTypes in safari only includes 'application/x-java-applet
   var versions = ['1.4.2', '1.5','1.6','1.7'],
       java_url = 'http://java.com/en/download/manual.jsp',
       version = "-1";

   function set_highest_version(potential_new_version){
     if(potential_new_version > version){
       version = potential_new_version;
     }
   }
   
   function populateVersions(){
     if(navigator.userAgent.indexOf("MSIE") != -1){
       populateVersionsIE();
     }
     else{
       populateVersionsNotIE();
     }
   }
   
   function populateVersionsIE(){

     // 1.5 -> 1.5.0.0
     function pad(version){
       var max_dots = 3;
       var dots = version.replace(/[^.]/g, '').length;
       for(; dots < max_dots; dots++){
         version = version + ".0";
       }
       return version;
     }
     
     for(var i = 0; i < versions.length; i++){
       var version = pad(versions[i]);
       
       try{
         if(new ActiveXObject('JavaWebStart.isInstalled.' + version) != null){
           set_highest_version(version);
         }
       }
       catch(e){
       }
     }
   }
   
   function populateVersionsNotIE(){
      if(navigator.mimeTypes){
        for(var i = 0; i < versions.length; i++){
          var version = versions[i];
          if(navigator.mimeTypes['application/x-java-applet;version=' + version]){      
            set_highest_version(version);
          }
        }
        // add the general one under ''
        if(navigator.mimeTypes['application/x-java-applet']){      
          set_highest_version("");
        }
      }
   }

   function hasVersion(min_version){
     console.log(min_version);

     if(navigator.vendor && (navigator.vendor.indexOf("Apple") !== -1)){
       console.log('safari');

       // safari
       return hasPlugin();
     }
     
     if(min_version !== undefined){
       console.log('version', version);

       return min_version <= version;
     }
     else{
       return hasPlugin();
     }
   }
  
  function hasPlugin(){
    return (version === "-1") ? false : true;
  }

  function paramify(key, value){
    return '  <param name="' + key + '" value="' + value + '" />';      
  }

  // init
  populateVersions();       

  $.applet = {

    plugin: hasPlugin(),
    hasVersion: hasVersion,
    version: version,
    java_url: java_url,

    inject: function(node, args){

      if(!this.plugin){
        if(args.noJava){
          args.noJava();
        }
        else{
          alert('Java is not present, please enable or download from ' + java.java_url);
        }
      }

      if(args.min_version){
        if(!hasVersion(args.min_version)){
          alert('Applet requires at least Java plugin version ' + args.min_version);
          return;
        }
      }

      var id = args.id,
          width = args.width || "1",
          heigth = args.heigth || "1",
          params = [];

      for(var k in args){
        params.push(paramify(k, args[k]));
      }

      var t = [
        '<object id="' + id + '" type="application/x-java-applet" width="' + width + '" height="' + heigth + '">',
        '  <param name="mayscript" value="true" />',
        params.join('\n'),
        '</object>'].join('\n');
      setTimeout(function(){
        node.innerHTML = t;
      }, 0);}
        
  };

})(window);