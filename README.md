## Afro Man
An open-source multiplayer Java game engine with no third-party libraries. Intended for the game Afro Man. 

There is a first-party [Console library](https://github.com/qwertysam/Console) that is used for reading the console while in-game. There are three third-party libraries *(see **Contributing**)* which are completely optional, and allow for Java to read MP3 files for the purpose of reducing file size. The game will run flawlessly without these libraries by using WAV files.

Please visit [the Wiki](https://github.com/qwertysam/afroman-client/wiki) for more info.

## Contributing
This setup assumes that you know how to add **JAR libraries** to a project through your IDE, and will skimp those instructions. They are tagged with **Lib**, and it is recommended to place the JARs in `./afroman-client/libs` in order to remain organized and avoid pushing to the repository with your libraries.

### Setup
1. Install the Java JDK 8 (or higher)
2. Install IntelliJ IDEA (or another IDE such as Eclipse)
3. Clone this repository `git clone https://github.com/qwertysam/afroman-client.git`
4. **Lib:** Get `Console.jar` from [here](https://github.com/qwertysam/Console/releases)

To support mp3 files...

5. **Lib:** Get the 1.4 compatible `tritonus_share.jar` from [here](http://www.tritonus.org/plugins.html)
6. **Lib:** Get the JLayer 1.0.1 zip (or tar.gz) from [here](http://www.javazoom.net/javalayer/sources.html) and extract `jl1.0.1.jar`
7. **Lib:** Get the MP3 SPI 1.9.5 zip (or tar.gz) from [here](http://www.javazoom.net/mp3spi/sources.html) and extract `mp3spi1.9.5.jar`

Now you can run Afro Man! Use your IDE to export the game to a runnable JAR, and...

8. Open the JAR file with an archive tool (e.g. 7zip, Winrar)
9. If you want to use the WAV file format for the audio, you can delete the `audio/mp3` folder from the archive. If you want to use the MP3 file format for the audio, you can delete the `audio/wav` folder from the archive. If you leave both, the WAV files will be used.

Now you have compiled Afro Man!
