# AntFarm
Defend the cheeses from the ants with the help of an anteater, bugkiller and odor neutralizer sprays.

![Ant Farm Gameplay](/demo.gif)

## Build
1. Clone repository
```
git clone https://github.com/szedenik-adam/AntFarm.git
```
2. Step into the repository's directory
```
cd AntFarm
```
3. Compile java files to class files
```
javac -encoding UTF-8 -cp src src\graphics\form.java
```
4. Run the program
```
java -cp src graphics.Form
```
5. Optional: Make jar file
```
jar cvmf JarManifest.txt AntFarm.jar -C bin .
```
6. Optional: Run jar file
```
java -jar AntFarm.jar
```

## Controls
**Left click**: Bug killer spray on the selected tile.

**Right click**: Odor neutralizer spray on the selected tile.

**Esc**: Open menu with the following options: resume game, new game, exit.

## Authors
Bálint Márton

Gyuris Bence

Herédi Péter

Szedenik Ádám

Széles Adorján