import 'dart:convert';

class DisplaySdkModel {
    String? displayType;

    DisplaySdkModel({
        this.displayType,
    });

    DisplaySdkModel copyWith({
        String? displayType,
    }) => 
        DisplaySdkModel(
            displayType: displayType ?? this.displayType,
        );

    factory DisplaySdkModel.fromRawJson(String str) => DisplaySdkModel.fromJson(json.decode(str));

    String toRawJson() => json.encode(toJson());

    factory DisplaySdkModel.fromJson(Map<String, dynamic> json) => DisplaySdkModel(
        displayType: json["displayType"],
    );

    Map<String, dynamic> toJson() => {
        "displayType": displayType,
    };
}
