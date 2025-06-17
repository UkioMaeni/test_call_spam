import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:test_call_spam/new_contact.dart';
import 'package:test_call_spam/service/spam_service.dart';

class CallAddPage extends StatefulWidget {
  final Function() onChange;
  const CallAddPage({super.key,required this.onChange});

  @override
  State<CallAddPage> createState() => _CallAddPageState();
}

class _CallAddPageState extends State<CallAddPage> {

  SpamService spamService = SpamService();

    List<SpamNumber>? customSpams;

    Future<void> getAllCustomNumbers()async{
      final numbers= await spamService.getAllScumCustomNumbers();
      customSpams=numbers;
      setState(() {
        
      });
    }

    @override
    void initState(){
      getAllCustomNumbers();
      super.initState();
    }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        toolbarHeight: 0,

      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          children: [
            Text(
                'Your contacts',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: Color.fromRGBO(31, 31, 31, 1)
                ),
              ),
            Builder(
              builder: (context) {
                if(customSpams==null){
                  return Expanded(child: Center(child: CircularProgressIndicator(),));
                }
                return Expanded(
                  child: Column(
                    children: [
                      Expanded(
                        child: Builder(
                          builder: (context) {
                            if(customSpams!.isEmpty){
                              return Image.asset("assets/people.png");
                            }
                            return SizedBox.shrink();
                          }
                        )
                      ),
                      GestureDetector(
                        onTap: () {
                          Navigator.push(context,MaterialPageRoute(builder: (context) => NewContact(onChange: widget.onChange,),));
                        },
                        child: Container(
                          height: 80,
                          width: double.infinity,
                          decoration: BoxDecoration(
                            color: Colors.blue
                          ),
                          child: Text(
                            "add new contact"
                          ),
                        ),
                      ),
                      SizedBox(height: 40,)
                    ],
                  ),
                );
                if(customSpams!.isEmpty){
                  Image.asset("assets/people.png");
                }
                return Image.asset("assets/people.png");
              },
            ),

          ],
        ),
      ),
    );
  }
}