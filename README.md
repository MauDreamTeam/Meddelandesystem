# Meddelandesystem

This program is intended to work similar to how MSN did and is developed as a group project for a university course.

## Development

Please read through this before writing anything to the codebase. The project uses maven as build system and it is IDE agnostic. For formatting code and to run the code you'll need to follow the guide below.

To get started you'll need either maven installed or IntelliJ.

**Using IntelliJ**

For IntelliJ you'll need to open the `pom.xml` file in this project and it'll prompt you if you want to open it as project or not. Choose `Open as Project`.

### Code Style

- The **package** name should be all lowercase.

- When formatting your code you'll need to use `clang-format` with the included `.clang-format`.

- The codebase use 4 spaces as indentation.

#### Getting clang-format

For **Windows** the clang-format can be found at https://releases.llvm.org/, head over to the latest `download` link, scroll down to **Pre-Built Binaries** and download the Windows one.

**NOTE (Windows Install)**: When installing make sure you select `Add LLVM to the system PATH for ...`. This makes sure that you can type `clang-format` in your terminal.

For the **macOS** you can use `brew` to install and **Linux** it is best to use your package manager.

To use `clang-format` with **IntelliJ**, there two ways. One is to use this extension [ClangFormat](https://plugins.jetbrains.com/plugin/13359-clangformat). This will add the option in `Code → Reformat Code with clang-format`, you'll be using this option instead of the regular `Reformat Code`.

The second one is the set up External Tools. This guide can be follow down below in [IntelliJ External Tools](#intellij-external-tools).


**NOTE (Windows)**: If the extension doesn't format your code you can go to `File → Settings... → Tools → clang-format`. In the `clang-format binary` you can try adding `clang-format.exe` instead. If it doesn't work you can specify the `PATH` that points to where `LLVM/bin` is in your system. If this doesn't fix your problem then you can follow the guide below on how to set up External Tools.

**NOTE IntelliJ**: If the extension doesn't work you can [setup an External Tool](#intellij-external-tools) that runs the clang-format for formatting the code. 

##### IntelliJ External Tools

To create a tool you can

1. Go to `File → Settings... → Tools → External Tools`.
2. Add a new tool by pressing the plus icon.
3. Give it a name such as `clang-format`.
4. In `Program:` field you take the whole path to where clang-format executable on your system. If your environment variable is already set you can just input `clang-format`.
5. In `Arguments:` you need to input these value `-i $FilePath$`.
6. In `Working directory:` you need `$FileDir$`

Now when you want to format your code you can go to `Tools → External Tools → clang-format` and it'll run the `clang-format` command for you and reformat the file you're currently looking at.

### Project Structure

```
Meddelandesystem
├─ src
│  ├─ main
│  │  └─ java
│  │     ├─ client
│  │     │  ├─ boundary
│  │     │  ├─ control
│  │     │  ├─ entity
│  │     │  └─ StartClient.java
│  │     ├─ server
│  │     │  ├─ control
│  │     │  ├─ entity
│  │     │  └─ StartServer.java
│  │     └─ shared
│  │        └─ entity
│  │           └─ User.java
│  └─ test
│     └─ java
├─ .clang-format
├─ .gitignore
├─ LICENSE
├─ pom.xml
└─ README.md
```

### How to run

**Using IntelliJ**

On IntelliJ you can go one of the mains and press start.

**Using Maven**

You can compile the code with this command

```shell
mvn compile
```

This will generate the the `class` files in `target/classes` directory. You can use these class files to run the main method.

**Start the server**

```shell
java -cp target/classes server.StartServer
```

**Start the client**

```shell
java -cp target/classes client.StartClient
```

### Generate JavaDoc

The javadoc can be generated in the following way. The output directory should be in `docs/apidocs`. The apidocs is ignored in the repo.

**Using maven**

Use this command to generate the javadoc

```
mvn javadoc:javadoc
```

This will created an html page in the `docs/apidocs`.

**Using Intellij**

Go to → Tools → Generate JavaDoc...

A Window will pop up, select `Whole project` and set `Output directory` to `docs/apidocs` and press `OK`. Javadoc will generate html docs for **you**.