

# How to use it

## 1. Get the latest dependency
[![Maven Central](
    https://maven-badges.herokuapp.com/maven-central/com.github.bogdanovmn.cmdline/cmdline-app/badge.svg
)]( https://maven-badges.herokuapp.com/maven-central/com.github.bogdanovmn.cmdline/cmdline-app)
```xml
<dependency>
    <groupId>com.github.bogdanovmn.cmdline</groupId>
    <artifactId>cmdline-app</artifactId>
    <version>...</version>
</dependency>
```

## 2. Create an entry point for your CLI-application.
```java
import com.github.bogdanovmn.cmdline.CmdLineAppBuilder;

public class App {
	public static void main(String[] args) throws Exception {
		new CmdLineAppBuilder(args)
			.withJarName("my-jar-name") // just for a help text (-h option) 
			.withDescription("My program does ...")
			
			// Optional argument
			.withArg("some-option", "...description of the option...")
			
			// Optional flag (without a value)
			.withFlag("flag", "...description of the option...")
			
			// If you need something what this wrapper doesn't support, you can pass original Apache's Option object
			.withCustomOption(customApacheCommandLineOption)
			
			// Mandatory option
			.withRequiredArg("mandatory-option", "...description of the option...")
			
			.withEntryPoint(
				cmdLine -> {
					if (cmdLine.hasOption("flag")) {
						// do something
					}
					if (cmdLine.hasOption("a")) {
						// do something
					}
				}
			).build().run();
	}
}
``` 

## 3. Run the application with -h flag in order to see the usage text (it will be automatically constructed)
```bash
usage: java -jar my-jar-name.jar [--custom-option] [-f] [-h] -m <ARG> [-s
       <ARG>]
My program does ...
    --custom-option            ...description of the option...
 -f,--flag                     ...description of the option...
 -h,--help                     show this message
 -m,--mandatory-option <ARG>   ...description of the option...
 -s,--some-option <ARG>        ...description of the option...
```

## 4. If must specify one of options, there is the easy solution: 
```java
new CmdLineAppBuilder(args)
    // Optional argument
    .withArg("a-option", "...")
    .withArg("b-option", "...")
    
    // "a-option" or "b-option" must be specified
    .withAtLeastOneRequiredOption("a-option", "b-option")
    
    .withEntryPoint(
        cmdLine -> {
            ...
        }
    ).build().run();
``` 
   