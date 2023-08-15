

# Why another CLI-library?

For my CLI applications I use Apache Commons-Cli library. It is powerful tool. Unfortunately, it is too verbose. 
This library allows you to use commons-cli in the fluent way. No more boilerplate code. 

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
    
            // Optional integer argument 
            .withInt("int-opt", "...description of the option...")
            
            // If you need something what this wrapper doesn't support, 
            // you can pass original Apache's Option object
            .withCustomOption(
                Option.builder()
                    .longOpt("custom-option")
                    .desc("...description of the option...")
            )
            
            // Mandatory option
            // note that we specified the short name directly (usually it is generating automatically)
            .withArg("mandatory-option", "...description of the option...")
                .withShort("m") 
                .required()
            
            .withEntryPoint(
                options -> {
                    if (options.getBool("flag")) {
                        // do something
                    }
                    if (options.has("int-opt")) {
                        // do something with options.getInt("int-opt")
                    }
                }
            )
        .build().run();
    }
}
``` 
Note: option's short name will be generated automatically

### 2.1 For a Spring CLI it would be like this
```java
import com.github.bogdanovmn.cmdlineapp.CmdLineAppBuilder;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        new CmdLineAppBuilder(args)
            // ...
            .withEntryPoint(
                options -> {
                    // ...
                }
            )
        .build().run();
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

# Features

## If you must specify one of options, there is a simple solution: 
```java
new CmdLineAppBuilder(args)
    // Optional argument
    .withArg("a-option", "...")
    .withArg("b-option", "...")
    
    // "a-option" or "b-option" must be specified
    .withAtLeastOneRequiredOption("a-option", "b-option")
    
    .withEntryPoint(
        options -> {
            ...
        }
    )
.build().run();
``` 

## If you want to connect options, there is a solution
```java
new CmdLineAppBuilder(new String[] {"-i", "123", "-b", "-s", "str"})
    .withArg("integer-opt", "integer option description")
    .withArg("string-opt", "string option description")
    .withFlag("bool-flag", "bool-flag description")
        .requires("integer-opt", "string-opt")
    .withEntryPoint(options -> {})
.build().run();
```
It means that if you specify "bool-flag" option, you must also specify it's dependencies:  "integer-opt" & "string-opt"
You don't need to manage it in your own code.

## Default values support
```java
new CmdLineAppBuilder(new String[] {})
    .withEnumArg("str-opt", "str-opt value description")
        .withDefault("defaul-value")
    
    .withEnumArg("int-opt", "int-opt value description")
        .withDefault(123)
    
    .withEntryPoint(options -> {
        if ("default-value".equals(options.get("str-opt"))) {
            // str-opt value is default
        }

        if (123 == options.getInt("int-opt"))) {
            // int-opt value is default
        }
    })
.build().run();
```
## Enum support with automatically description of all possible values
```java
...
enum MyEnum { FOO, BAR }
...
new CmdLineAppBuilder(new String[] {"-e", "FOO"})
    .withEnumArg("enum-value", "enum value description", MyEnum.class)
    
    .withEnumArg("enum2", "enum2 value description", MyEnum.class)
        .withDefault(BAR)
    
    .withEntryPoint(options -> {
        if ("FOO".equals(options.getEnumAsRawString("enum-value"))) {
            // it is FOO
        }

        if (FOO == options.getEnum("enum-value"))) {
            // it is FOO
        }

        if (BAR == options.getEnum("enum2"))) {
            // it is BAR
        }
    })
.build().run();
```
