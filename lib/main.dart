import 'dart:developer';
import 'dart:math' as math;

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:my_native_plugin/my_native_plugin.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:test_call_spam/call.dart';
import 'package:test_call_spam/call_juranal_page.dart';
import 'package:test_call_spam/db_dounload.dart';
import 'package:test_call_spam/service/spam_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the 2theme of your application.
        //
        // TRY THIS: Try running your application with "flutter run". You'll see
        // the application has a purple toolbar. Then, without quitting the app,
        // try changing the seedColor in the colorScheme below to Colors.green
        // and then invoke "hot reload" (save your changes or press the "hot
        // reload" button in a Flutter-supported IDE, or press "r" if you used
        // the command line to start the app).
        //
        // Notice that the counter didn't reset back to zero; the application
        // state is not lost during the reload. To reset the state, use hot
        // restart instead.
        //
        // This works for code too, not just values: Most code changes can be
        // tested with just a hot reload.
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".
 
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;


  MyNativePlugin plagin = MyNativePlugin();

  bool? pluginIsGranted;

  Future<void> checkGrantedPlagin()async{
    bool isRolled = await plagin.isCallScreeningRoleHeld();
    bool isServiceON = await plagin.getEnableBlocking();
    if(isRolled&&isServiceON){
      pluginIsGranted=true;
    }else{
      pluginIsGranted=false;
    }
    
    if(mounted){
      setState(() {
      
    });
    }
  }

  sda()async{
    await Permission.notification.request();
    log((await Permission.notification.isGranted).toString());
  }

  @override
  void initState() {
    checkGrantedPlagin();
    sda();
    super.initState();
  }


  bool isToogleProcess=false;

  void toggleSpam()async{
    log("isToogleProcess");
    log(isToogleProcess.toString());
    try {
      
      if(isToogleProcess) return;
      isToogleProcess=true;
      log("pluginIsGranted");
      log(pluginIsGranted.toString());
      if(pluginIsGranted!){
        await plagin.setEnableBlocking(false);
        if(mounted){
          setState(() {
            pluginIsGranted=false;
            
          });
        }
      }else{
        final isRolled =  await SpamService().isCallScreeningRoleHeld();
        log(isRolled.toString());
        if(!isRolled){
          await SpamService().requestCallSucurePage();
          
        }
        final isRolledCheck =  await SpamService().isCallScreeningRoleHeld();
        log(isRolledCheck.toString());
        if(isRolledCheck){
          await plagin.setEnableBlocking(true);
          if(mounted){
            setState(() {
              pluginIsGranted=true;
              
            });
          }
        }
      }
      
    } catch (e) {
      log(e.toString());
    }finally{
      isToogleProcess=false;
    }
    
  }

  // Future<bool> isCallScreeningRoleHeld()async{
  //   final isRolled =  await SpamService().isCallScreeningRoleHeld();
  // }


  bool tresholdIsView = false;
  String  tresholdVariable = "one";
  @override
  Widget build(BuildContext context) {

    return Scaffold(
      backgroundColor:pluginIsGranted==null|| !pluginIsGranted!? Color.fromRGBO(230, 235, 255, 1):Color.fromRGBO(244, 255, 250, 1),
      appBar: AppBar(
          backgroundColor:pluginIsGranted==null|| !pluginIsGranted!? Color.fromRGBO(230, 235, 255, 1):Color.fromRGBO(244, 255, 250, 1),
          toolbarHeight: 0,
      ),
      body: Builder(
        builder: (context) {
          if(pluginIsGranted==null){
            return Center(child: CircularProgressIndicator(),);
          }
          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                SizedBox(height: 62,),
                Align(
                  alignment: Alignment.center,
                  child: GestureDetector(
                    onTap: toggleSpam,
                    child: Container(
                      height: 200,
                      width: 200,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(200),
                        boxShadow: [
                          BoxShadow(blurRadius: 30,color: Color.fromARGB(26, 0, 0, 0),spreadRadius: 0,offset: Offset(0, 4))
                        ]
                      ),
                      child: Icon(Icons.power_settings_new,size: 40,),
                    ),
                  ),
                ),
                
                Align(
                  alignment: Alignment.center,
                  child: Text(
                    'Protection paused',
                    style: TextStyle(
                      fontSize: 16
                    ),
                  ),
                ),
                SizedBox(height: 22,),
                Text(
                  'Your SPAM statistics',
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.w700,
                    color: Color.fromRGBO(31, 31, 31, 1)
                  ),
                ),
                SizedBox(height: 16,),
                Row(
                  children: [
                    Expanded(
                      child: Container(
                        height: 84,
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(16)
                        ),
                        padding: EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              'SMS:',
                              style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w700,
                                color: Color.fromRGBO(31, 31, 31, 1)
                              ),
                            ),
                            Text(
                              156.toString(),
                              style: TextStyle(
                                fontSize: 22,
                                fontWeight: FontWeight.w700,
                                color: Color.fromRGBO(31, 31, 31, 1)
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    SizedBox(width: 16,),
                    Expanded(
                      child: Container(
                        height: 84,
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(16)
                        ),
                        padding: EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              'SMS:',
                              style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w700,
                                color: Color.fromRGBO(31, 31, 31, 1)
                              ),
                            ),
                            Text(
                              156.toString(),
                              style: TextStyle(
                                fontSize: 22,
                                fontWeight: FontWeight.w700,
                                color: Color.fromRGBO(31, 31, 31, 1)
                              ),
                            ),
                          ],
                        ),
                      ),
                    )
                  ],
                ),
                SizedBox(height: 32,),
                Container(
                  constraints: BoxConstraints(
                    minHeight: 56
                  ), 
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(16)
                  ),
                  padding: EdgeInsets.all(16),
                  child: Column(
                    children: [
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            tresholdIsView=!tresholdIsView;
                          });
                        },
                        child: Container(
                          color: Colors.transparent,
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(
                                'Risk threshold',
                                style: TextStyle(
                                  fontSize: 17,
                                  fontWeight: FontWeight.w600,
                                  color: Color.fromRGBO(31, 31, 31, 1)
                                ),
                              ),
                              Transform.rotate(
                                angle: tresholdIsView?math.pi/2:2*math.pi,
                                child: Icon(Icons.chevron_right)
                              )
                            ],
                          ),
                        ),
                      ),
                      if(tresholdIsView)
                      Column(
                        children: [
                          SizedBox(height: 20,),
                          GestureDetector(
                            onTap: () {
                              tresholdVariable="one";
                              setState(() {
                                
                              });
                            },
                            child: Container(
                              constraints: BoxConstraints(
                                minHeight: 30
                              ),
                              child: Row(
                                children: [
                                  Container(
                                    width: 18,
                                    height: 18,
                                    decoration: BoxDecoration(
                                      border: Border.all(
                                        color: Colors.black,
                                        width: 2
                                      ),
                                      borderRadius: BorderRadius.circular(18)
                                    ),
                                    alignment: Alignment.center,
                                    child: Builder(
                                      builder: (context) {
                                        if(tresholdVariable=="one"){
                                          return Container(
                                            height: 10,
                                            width: 10,
                                            decoration: BoxDecoration(
                                              color: Colors.black,
                                              borderRadius: BorderRadius.circular(10)
                                            ),
                                          );
                                        }
                                        return SizedBox.shrink();
                                      }
                                    ),
                                  ),
                                  SizedBox(width: 7,),
                                  Text(
                                    'Block all spam and scam calls',
                                    style: TextStyle(
                                      fontSize: 15,
                                      fontWeight: FontWeight.w600,
                                      color: Color.fromRGBO(31, 31, 31, 1)
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                          Opacity(
                            opacity: 0.5,
                            child: Container(
                              constraints: BoxConstraints(
                                minHeight: 30
                              ),
                              child: Row(
                                children: [
                                  Container(
                                    width: 18,
                                    height: 18,
                                    decoration: BoxDecoration(
                                      border: Border.all(
                                        color: Colors.black,
                                        width: 2
                                      ),
                                      borderRadius: BorderRadius.circular(18)
                                    ),
                                    alignment: Alignment.center,
                                    child: Builder(
                                      builder: (context) {
                                        if(tresholdVariable=="two"){
                                          return Container(
                                            height: 10,
                                            width: 10,
                                            decoration: BoxDecoration(
                                              color: Colors.black,
                                              borderRadius: BorderRadius.circular(10)
                                            ),
                                          );
                                        }
                                        return SizedBox.shrink();
                                      }
                                    ),
                                  ),
                                  SizedBox(width: 7,),
                                  Expanded(
                                    child: Text(
                                      'Show warning for spam and scam calls (coming soon)',
                                      style: TextStyle(
                                        fontSize: 15,
                                        fontWeight: FontWeight.w600,
                                        color: Color.fromRGBO(31, 31, 31, 1)
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ],
                      )
                    ],
                  ),
                  
                ),
                SizedBox(height: 8,),
                GestureDetector(
                  onTap: () {
                    Navigator.push(context, MaterialPageRoute(builder: (context) => CallPage(),));
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.white,
                    ),
                    padding: EdgeInsets.all(16),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          'Allow list',
                          style: TextStyle(
                            fontSize: 17,
                            fontWeight: FontWeight.w600,
                            color: Color.fromRGBO(31, 31, 31, 1)
                          ),
                        ),
                        Icon(Icons.chevron_right)
                      ],
                    ),
                  ),
                ),
                SizedBox(height: 8,),
                GestureDetector(
                  onTap: () {
                    Navigator.push(context, MaterialPageRoute(builder: (context) => DbDownloadPage(),));
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.white,
                    ),
                    padding: EdgeInsets.all(16),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          'DbDownloadPage',
                          style: TextStyle(
                            fontSize: 17,
                            fontWeight: FontWeight.w600,
                            color: Color.fromRGBO(31, 31, 31, 1)
                          ),
                        ),
                        Icon(Icons.chevron_right)
                      ],
                    ),
                  ),
                ),
                SizedBox(height: 8,),
                GestureDetector(
                  onTap: () {
                    Navigator.push(context, MaterialPageRoute(builder: (context) => CallJurnalPage(),));
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.white,
                    ),
                    padding: EdgeInsets.all(16),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          'CallJurnalPage',
                          style: TextStyle(
                            fontSize: 17,
                            fontWeight: FontWeight.w600,
                            color: Color.fromRGBO(31, 31, 31, 1)
                          ),
                        ),
                        Icon(Icons.chevron_right)
                      ],
                    ),
                  ),
                ),
                // SizedBox(height: 40,),
                // Container(
                //   height: 50,
                //   width: double.infinity,
                //   decoration: BoxDecoration(
                //     color: Colors.blue
                //   ),
                //   alignment: Alignment.center,
                //   child: Text(
                //     "View logs"
                //   ),
                // ),
              ],
            ),
          );
        }
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
