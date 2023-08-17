

[![Maven Central](
https://maven-badges.herokuapp.com/maven-central/com.github.bogdanovmn.cmdline/cmdline-app/badge.svg
)]( https://maven-badges.herokuapp.com/maven-central/com.github.bogdanovmn.cmdline/cmdline-app)

# Why another CLI-library?

This library aims to enable the usage of Apache Commons-CLI in a fluent manner with added convenience features, eliminating the need for boilerplate code.

# Table of contents
- [**Features**](#features)
- [**How to use it**](#how-to-use-it)
- [**Examples**](#examples)
  - [**If you must use one of not required options**](#if-you-must-use-one-of-not-required-options)
  - [**If you want to connect options**](#if-you-want-to-connect-options)
  - [**Default values support**](#default-values-support)
  - [**Enum support with automatically description of all possible values**](#enum-support-with-automatically-description-of-all-possible-values)
  - [**Mutual exclusions for options allows you to prevent the use of one option if another option is set**](#mutual-exclusions-for-options-allows-you-to-prevent-the-use-of-one-option-if-another-option-is-set)
  - [**If you need something what this wrapper doesn't support, just pass original Apache's Option object**](#if-you-need-something-what-this-wrapper-doesnt-support-just-pass-original-apaches-option-object)
- [**Real-world example**](#real-world-example)
  - [**Configuration**](#configuration)
  - [**Usage output**](#usage-output)

# Features
* Fluent API
* All boilerplate code is hidden under the hood
## Restrictions
* Mutual exclusions for options
* At least one of specified not required options has to be used
* Options dependencies
## Convenient
* Option's short name auto-generation
* Option's value types support
* Default value during an option's definition level support
* Enum type for options support
* Usage text (--help) auto-configuration with default values and Enum's possible values description

# How to use it

## 1. Get the latest dependency
```xml
<dependency>
    <groupId>com.github.bogdanovmn.cmdline</groupId>
    <artifactId>cmdline-app</artifactId>
    <version>3.1.1</version>
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
            .withArgInt("int-option", "...description of the option...")
            
            // Mandatory option
            .withArg("mandatory-option", "...description of the option...")
                .withShort("m") // option's short name definition (by default, it is generating automatically)
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

## 3. Run the application with -h flag in order to see the usage text (it will be automatically constructed)
```bash
usage: java -jar my-jar-name.jar [-f] [-h] -m <STR> [-s <STR>] [-i <INT>]
My program does ...
 -i,--int-option <INT>         ...description of the option...
 -f,--flag                     ...description of the option...
 -m,--mandatory-option <STR>   ...description of the option...
 -s,--some-option <STR>        ...description of the option...
 -h,--help                     show this message
```

# Examples
## If you must use one of not required options 
```java
new CmdLineAppBuilder(args)
    .withArg("required-opt", "...").required()
    // Optional argument
    .withArg("a-opt", "...")
    .withArg("b-opt", "...")
    
    // "a-opt" or "b-opt" must be specified
    .withAtLeastOneRequiredOption("a-opt", "b-opt")
    
    .withEntryPoint(
        options -> {...}
    )
.build().run();
``` 

## If you want to connect options
```java
new CmdLineAppBuilder(args)
    .withArg("a-opt", "...")
    .withArg("b-opt", "...")
    .withFlag("c-opt", "...")
        .requires("a-opt", "b-opt")
    .withEntryPoint(options -> {...})
.build().run();
```
It means that if you specify "c-opt" option, you must also specify it's dependencies:  "a-opt" & "b-opt"
You don't need to manage it in your own code.

## Default values support
```java
new CmdLineAppBuilder(args)
    .withArg("str-opt", "...")
        .withDefault("defaul-value")
    
    .withIntArg("int-opt", "...")
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

## Mutual exclusions for options allows you to prevent the use of one option if another option is set
### Example 1
```java
new CmdLineAppBuilder(args)
    .withArg("a-opt", "...")
    .withArg("b-opt", "...")

    .withMutualExclusions("a-opt", "b-opt")
    
    .withEntryPoint(options -> {...})
.build().run();
```
There you can use either the a-opt or the b-opt
### Example 2
```java
new CmdLineAppBuilder(args)
    .withArg("a-opt", "...")
    .withArg("b-opt", "...")
    .withArg("c-opt", "...")

    .withMutualExclusions(
        "a-opt", 
        List.of("b-opt", "c-opt")
    )
    
    .withEntryPoint(options -> {...})
.build().run();
```
There you can use either the a-opt or one of these: b-opt or c-opt

## If you need something what this wrapper doesn't support, just pass original Apache's Option object
```java
.withCustomOption(
    Option.builder()
        .longOpt("custom-option")
        .desc("...description of the option...")
        ...
)
```

# Real-world example
## Configuration
```java
public class App {
    private static final String OPT_INDEX_FILE = "index-file";
    private static final String OPT_SEARCH_TITLE_TERM = "search-title-term";
    private static final String OPT_SEARCH_AUTHOR_TERM = "search-author-term";
    private static final String OPT_SEARCH_ENGINE = "search-engine";
    private static final String OPT_ARCHIVE_DIR = "archive-dir";
    private static final String OPT_SEARCH_ENGINE_URL = "search-engine-dir";
    private static final String OPT_SEARCH_ENGINE_CREATE_INDEX = "search-engine-create-index";
    private static final String OPT_SEARCH_MAX_RESULTS = "search-max-results";
    private static final String OPT_EXPORT_BY_ID = "export-book-by-id";
    private static final String OPT_EXPORT_TO = "export-to";
    private static final String OPT_SHOW_STATISTIC = "show-statistic";


    private static final int MAX_RESULTS_DEFAULT = 30;

    public static void main(String[] args) throws Exception {

        new CmdLineAppBuilder(args)
            .withJarName("inpx-tool")
            .withDescription("INPX file browser")

            .withArg(OPT_INDEX_FILE, "an index file name")
                .required()

            .withEnumArg(OPT_SEARCH_ENGINE, "search engine", SearchEngineMethod.class)
                .withDefault(SearchEngineMethod.SIMPLE)

            .withIntArg(OPT_SEARCH_MAX_RESULTS, "search max results")
                .withDefault(MAX_RESULTS_DEFAULT)

            .withIntArg(OPT_EXPORT_BY_ID, "export FB2 file by id")
                .requires(
                    OPT_EXPORT_TO,
                    OPT_ARCHIVE_DIR
                )
            .withArg(OPT_ARCHIVE_DIR, "an archive directory path")
            .withArg(OPT_EXPORT_TO, "export FB2 file target directory")

            .withArg(OPT_SEARCH_TITLE_TERM, "a search query title term")
            .withArg(OPT_SEARCH_AUTHOR_TERM, "a search query author term")
            .withArg(OPT_SEARCH_ENGINE_URL, "search engine index directory (only for Lucene engine)")
            .withFlag(OPT_SEARCH_ENGINE_CREATE_INDEX, "create search index (only for Lucene engine)")
                .requires(OPT_SEARCH_ENGINE_URL)

            .withFlag(OPT_SHOW_STATISTIC, "show books statistic")

            .withAtLeastOneRequiredOption(
                OPT_EXPORT_BY_ID,
                OPT_SEARCH_AUTHOR_TERM,
                OPT_SEARCH_TITLE_TERM,
                OPT_SEARCH_ENGINE_CREATE_INDEX,
                OPT_SHOW_STATISTIC
            )

            .withMutualExclusions(
                OPT_EXPORT_BY_ID,
                OPT_SEARCH_ENGINE_CREATE_INDEX,
                OPT_SHOW_STATISTIC,
                List.of(
                    OPT_SEARCH_AUTHOR_TERM,
                    OPT_SEARCH_TITLE_TERM
                )
            )

            .withEntryPoint(
                options -> {
                    InpxFile booksIndex = new InpxFile(options.get(OPT_INDEX_FILE));

                    if (options.getBool(OPT_SHOW_STATISTIC)) {
                        showStatistic(booksIndex);
                    } else if (options.has(OPT_EXPORT_BY_ID)) {
                        exportToFile(options);
                    } else if (options.getBool(OPT_SEARCH_ENGINE_CREATE_INDEX)) {
                        createLuceneIndex(
                            searchEngine(options, booksIndex)
                        );
                    } else if (options.has(OPT_SEARCH_TITLE_TERM) || options.has(OPT_SEARCH_AUTHOR_TERM)) {
                        searchBooks(
                            searchEngine(options, booksIndex),
                            SearchQuery.builder()
                                .author(options.get(OPT_SEARCH_AUTHOR_TERM))
                                .title(options.get(OPT_SEARCH_TITLE_TERM))
                            .build()
                        );
                    }
                }
            ).build().run();
    }
}
```
## Usage output
```
usage: java -jar inpx-tool.jar -i <STR> [-s <ENUM>] [-smr <INT>] [-e <INT>] [-a <STR>] [-et <STR>] [-stt <STR>] [-sat
       <STR>] [-sed <STR>] [-seci] [-ss] [-h]
INPX file browser
  -i,--index-file <STR>                an index file name
  -s,--search-engine <ENUM>            search engine
                                       Possible values: SIMPLE | FUZZY | LUCENE
                                       Default: SIMPLE
  -smr,--search-max-results <INT>      search max results
                                       Default: 30
  -e,--export-book-by-id <INT>         export FB2 file by id
  -a,--archive-dir <STR>               an archive directory path
  -et,--export-to <STR>                export FB2 file target directory
  -stt,--search-title-term <STR>       a search query title term
  -sat,--search-author-term <STR>      a search query author term
  -sed,--search-engine-dir <STR>       search engine index directory (only for Lucene engine)
  -seci,--search-engine-create-index   create search index (only for Lucene engine)
  -ss,--show-statistic                 show books statistic
  -h,--help                            show this message
```