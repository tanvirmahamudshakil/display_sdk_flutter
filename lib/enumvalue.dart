// ignore_for_file: constant_identifier_names, non_constant_identifier_names

enum DisplayType { PD108, PD220, PD280, PD350, PD500, PD700 }

List<int> BaudrateLed = [2400, 4800, 9600];

List<String> statusLight = [
  "None",
  "Price",
  "Total",
  "Collect",
  "Change",
];

List<int> cursorpot = [1, 2, 3, 4, 5, 6, 7, 8];

int PRINTER80LEDTEXTTOTAL = 1;
int PRINTER80LEDTEXTPRICE = 2;
int PRINTER80LEDTEXTCHANGE = 3;
