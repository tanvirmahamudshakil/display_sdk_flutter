import 'package:display_sdk_flutter/display_sdk_flutter.dart';
import 'package:display_sdk_flutter/enumvalue.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

class LedDisplay extends StatefulWidget {
  const LedDisplay({super.key});

  @override
  State<LedDisplay> createState() => _LedDisplayState();
}

class _LedDisplayState extends State<LedDisplay> {
  final _displaySdk = DisplaySdkFlutter();

  List<String> serialPort = [];
  String? selectserialport;
  int? selectboundrate;

  getusbFile() async {
    serialPort = await _displaySdk.serialPortFinder();
    setState(() {});
  }

  void connect() async {
    if (selectserialport != null && selectboundrate != null) {
      var data = await _displaySdk.displayConnectSdk(serialPort: selectserialport!, serialBaudrate: selectboundrate!);
      showsnakbar("${data}");
    } else {
      showsnakbar("serial port and selectboundrate must be select");
      // Get.snackbar("Warning", "serial port and selectboundrate must be select")
      //     .show();
    }
  }

  void letdisplay() async {
    var manageExternalStorage = await Permission.manageExternalStorage.status;
    var storage = await Permission.storage.status;
    if (manageExternalStorage.isDenied) {
      await Permission.manageExternalStorage.request();
    }

    if (storage.isDenied) {
      await Permission.storage.request();
    }
  }

  showsnakbar(String text) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(text)));
  }

  TextEditingController textEditingController = TextEditingController();

  @override
  void initState() {
    _displaySdk.setDisplayType(displayType: DisplayType.PD108.name);
    letdisplay();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      bottomNavigationBar: connectBox(),
      appBar: AppBar(
        title: Text("Led"),
      ),
      body: Row(
        children: [
          SizedBox(
            width: 160,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextFormField(
                  keyboardType: TextInputType.number,
                  controller: textEditingController,
                  decoration: const InputDecoration(
                    hintText: "Enter any Number",
                    border: OutlineInputBorder(),
                    isDense: true,
                  ),
                )
              ],
            ),
          ),
          Column(
            children: [
              MaterialButton(
                color: Colors.orange,
                onPressed: () async {
                  print("sdvsdvb ${textEditingController.text}");
                  await _displaySdk.ledStatusLight(status: 2);
                  var data = await _displaySdk.displayText(text: textEditingController.text);
                  showsnakbar("$data");
                },
                child: const Text("Display"),
              ),
              SizedBox(height: 10),
              MaterialButton(
                color: Colors.orange,
                onPressed: () async {
                  var data = await _displaySdk.ledInit();
                  showsnakbar("${data}");
                },
                child: const Text("INIT"),
              ),
              SizedBox(height: 10),
              MaterialButton(
                color: Colors.orange,
                onPressed: () async {
                  var data = await _displaySdk.clearScreen();
                  showsnakbar("${data}");
                },
                child: const Text("CLEAN SCREEN"),
              ),
              SizedBox(height: 10),
              MaterialButton(
                color: Colors.orange,
                onPressed: () async {
                  var data = await _displaySdk.clearLine();
                  showsnakbar("${data}");
                },
                child: const Text("CLEAR LINE"),
              ),
              MaterialButton(
                color: Colors.orange,
                onPressed: () {
                  getusbFile();
                },
                child: const Text("USB GET"),
              ),
            ],
          )
        ],
      ),
    );
  }

  Widget connectBox() {
    return Container(
      padding: EdgeInsets.all(10),
      color: Colors.indigo,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Row(
            children: [
              Container(
                width: 150,
                child: DropdownButtonFormField(
                  decoration: InputDecoration(border: OutlineInputBorder(), isDense: true, contentPadding: EdgeInsets.all(10)),
                  hint: Text(
                    "Select USB",
                    style: TextStyle(color: Colors.white),
                  ),
                  items: List.generate(serialPort.length, (index) {
                    var data = serialPort[index];
                    return DropdownMenuItem(child: Text(data), value: index);
                  }),
                  onChanged: (value) {
                    setState(() {
                      selectserialport = serialPort[value!];
                    });
                  },
                ),
              ),
              SizedBox(width: 10),
              SizedBox(
                width: 150,
                child: DropdownButtonFormField(
                  decoration: InputDecoration(border: OutlineInputBorder(), isDense: true, contentPadding: EdgeInsets.all(10)),
                  hint: const Text(
                    "Boundrate",
                    style: TextStyle(color: Colors.white),
                  ),
                  items: List.generate(BaudrateLed.length, (index) {
                    var data = BaudrateLed[index];
                    return DropdownMenuItem(child: Text(data.toString()), value: index);
                  }),
                  onChanged: (value) {
                    setState(() {
                      selectboundrate = BaudrateLed[value!];
                    });
                  },
                ),
              )
            ],
          ),
          SizedBox(height: 10),
          TextButton(
              style: TextButton.styleFrom(backgroundColor: Colors.red),
              onPressed: () {
                connect();
              },
              child: Text(
                "Connected",
                style: TextStyle(color: Colors.white),
              ))
        ],
      ),
    );
  }
}
