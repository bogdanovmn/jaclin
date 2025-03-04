<p align="center"><img src="logo.png" alt="jaclin"></p>

# Jaclin: why another CLI-library?

This library aims to work with command line arguments in a fluent manner with added convenience features, eliminating the need for boilerplate code.

# Table of contents
- [**Features**](#features)
- [**How to use it**](#how-to-use-it)
- [**Examples**](#examples)
  - [**If you must use one of not required options**](#if-you-must-use-one-of-not-required-options)
  - [**If you want to connect options**](#if-you-want-to-connect-options)
  - [**Default values support**](#default-values-support)
  - [**Enum support with automatically description of all possible values**](#enum-support-with-automatically-description-of-all-possible-values)
  - [**Mutual exclusions for options allows you to prevent the use of one option if another option is set**](#mutual-exclusions-for-options-allows-you-to-prevent-the-use-of-one-option-if-another-option-is-set)
- [**Real-world example**](#real-world-example)
  - [**Configuration**](#configuration)
  - [**Usage output**](#usage-output)

# Features
* Fluent lightweight API
* No boilerplate code, just say what options you need and enjoy
* Convenient: option's short name auto-generation
* Convenient: option's value types support
* Convenient: default value during an option's definition level support
* Convenient: enum type for options support
* Convenient: usage text (--help) auto-configuration with default values and Enum's possible values description
* Restrictions: mutual exclusions for options
* Restrictions: at least one of specified not required options has to be used
* Restrictions: options dependencies

# How to use it

## 1. Get the latest dependency
[![Maven Central](
https://maven-badges.herokuapp.com/maven-central/com.github.bogdanovmn.jaclin/jaclin/badge.svg
)]( https://maven-badges.herokuapp.com/maven-central/com.github.bogdanovmn.jaclin/jaclin)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fbogdanovmn%2Fjaclin.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fbogdanovmn%2Fjaclin?ref=badge_shield)
```xml
<dependency>
    <groupId>com.github.bogdanovmn.jaclin</groupId>
    <artifactId>jaclin</artifactId>
    <version>4.0.2</version>
</dependency>
```

## 2. Create an entry point for your CLI-application.

```java
import com.github.bogdanovmn.jaclin.CLI;

public class App {
    public static void main(String[] args) throws Exception {
        new CLI("my-jar-name", "My program does ...")
            .withRequiredOptions()
                .strArg("mandatory-option", "...description of the option...")
                    .withShortName("m") // option's short name definition (by default, it is generating automatically)
            .withOptions() // Not required options
                .strArg("some-option", "...description of the option...")
                // Optional flag (without a value)
                .flag("flag", "...description of the option...")
                // Optional integer argument 
                .intArg("int-option", "...description of the option...")
            .withEntryPoint(
                options -> {
                    if (options.enabled("flag")) {
                        // do something
                    }
                    if (options.has("int-opt")) {
                        // do something with options.getInt("int-opt")
                    }
                }
            )
            .run(args);
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
.withOptions()
    .strArg("a-opt", "...")
    .strArg("b-opt", "...")
.withRestrictions()
    // "a-opt" or "b-opt" must be specified
    .atLeastOneShouldBeUsed("a-opt", "b-opt")
``` 

## If you want to connect options
```java
.withOptions()
    .strArg("a-opt", "...")
    .strArg("b-opt", "...")
    .flag("c-opt", "...")
        .requires("a-opt", "b-opt")
```
It means that if you specify "c-opt" option, you must also specify it's dependencies:  "a-opt" & "b-opt"
You don't need to manage it in your own code.

## Default values support
```java
.withOptions()
    .strArg("str-opt", "...")
        .withDefault("defaul-value")
    
    .intArg("int-opt", "...")
        .withDefault(123)
    
.withEntryPoint(
    options -> {
        if ("default-value".equals(options.get("str-opt"))) {
            // str-opt value is default
        }
        if (123 == options.getInt("int-opt"))) {
            // int-opt value is default
        }
    }
).run(); // no args provided
```
## Enum support with automatically description of all possible values
```java
...
enum MyEnum { FOO, BAR }
...
.withOptions()
    .enumArg("enum-value", "enum value description", MyEnum.class)
    .enumArg("enum2", "enum2 value description", MyEnum.class)
        .withDefault(BAR)
.withEntryPoint(
    options -> {
        if ("FOO".equals(options.getEnumAsRawString("enum-value"))) {
            // it is FOO
        }
        if (FOO == options.getEnum("enum-value"))) {
            // it is FOO
        }
        if (BAR == options.getEnum("enum2"))) {
            // it is BAR
        }
    }
).run("-e", "FOO");
```

## Mutual exclusions for options allows you to prevent the use of one option if another option is set
### Example 1
```java
.withOptions()
    .strArg("a-opt", "...")
    .strArg("b-opt", "...")
.withRestrictions()
    .mutualExclusions("a-opt", "b-opt")
```
There you can use either the a-opt or the b-opt
### Example 2
```java
.withOptions()
    .strArg("a-opt", "...")
    .strArg("b-opt", "...")
    .strArg("c-opt", "...")
.withRestrictions()
    .mutualExclusions(
        "a-opt", 
        List.of("b-opt", "c-opt")
    )
```
There you can use either the a-opt or one of these: b-opt or c-opt

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

        new CLI("inpx-tool", "INPX file browser")
            .withRequiredOptions()
                .strArg(OPT_INDEX_FILE, "an index file name")

            .withOptions()
                .enumArg(OPT_SEARCH_ENGINE, "search engine", SearchEngineMethod.class)
                    .withDefault(SIMPLE)

                .intArg(OPT_SEARCH_MAX_RESULTS, "search max results")
                    .withDefault(MAX_RESULTS_DEFAULT)
    
                .intArg(OPT_EXPORT_BY_ID, "export FB2 file by id")
                    .requires(OPT_EXPORT_TO, OPT_ARCHIVE_DIR)
                
                .strArg(OPT_ARCHIVE_DIR, "an archive directory path")
                .strArg(OPT_EXPORT_TO, "export FB2 file target directory")
    
                .strArg(OPT_SEARCH_TITLE_TERM, "a search query title term")
                .strArg(OPT_SEARCH_AUTHOR_TERM, "a search query author term")
                .strArg(OPT_SEARCH_ENGINE_URL, "search engine index directory (only for Lucene engine)")
                .flag(OPT_SEARCH_ENGINE_CREATE_INDEX, "create search index (only for Lucene engine)")
                    .requires(OPT_SEARCH_ENGINE_URL)
    
                .flag(OPT_SHOW_STATISTIC, "show books statistic")
            
            .withRestrictions()
                .atLeastOneShouldBeUsed(
                    OPT_EXPORT_BY_ID,
                    OPT_SEARCH_AUTHOR_TERM,
                    OPT_SEARCH_TITLE_TERM,
                    OPT_SEARCH_ENGINE_CREATE_INDEX,
                    OPT_SHOW_STATISTIC
                )
                .mutualExclusions(
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

                    if (options.enabled(OPT_SHOW_STATISTIC)) {
                        showStatistic(booksIndex);
                    } else if (options.has(OPT_EXPORT_BY_ID)) {
                        exportToFile(options);
                    } else if (options.enabled(OPT_SEARCH_ENGINE_CREATE_INDEX)) {
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
            ).run(args);
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

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fbogdanovmn%2Fjaclin.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fbogdanovmn%2Fjaclin?ref=badge_large)