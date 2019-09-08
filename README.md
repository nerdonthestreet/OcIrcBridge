# OcIrcBridge

OcIrcBridge is a Java program that provides a bridge between a Composr chat room and an IRC channel. It was written by Jacob Kauffmann at Nerd on the Street, who could not find a pre-existing solution for bridging chat between Composr and other platforms.

## Setup

This bot interacts with your Composr installation in two ways: HTTP requests, and MySQL polling. For the HTTP requests, you will need to create a user on your Composr website for the bot to log in as. For the MySQL polling, you will need to create an account on your MariaDB or MySQL server for OcIrcBridge to use.

For security reasons, it is recommended that you restrict this MySQL/MariaDB account's access to only the Composr chat messages table (called cms\_chat\_messages by default), and only allow this user to log in from the IP address of the server running OcIrcBridge. The bot only needs read access to this table; it will never write there directly.

Sent messages are posted through Composr via HTTP. Because we are authenticating, all interactions with Composr are hard-coded to use HTTPS (TLS), so you will need to have TLS set up for your website in order for this bot to work without modifying the code. You can get free TLS certificates using Let's Encrypt: [https://letsencrypt.org/](https://letsencrypt.org/)

## Configuration

The majority of configuration is located in an easy-to-use configuration file. You can find an example file with all possible settings in this repository, called "OcIrcProperties-properties.config". The configuration file is heavily commented with explanations of each option.

## Usage

Once you have filled out the configuration file, simply run the JAR file (after building OcIrcBridge, if necessary) with the following syntax:

'java -jar ./OcIrcBridge-###.jar ./OcIrcBridge-properties.config'

As seen above, you just need to run the JAR file in Java, and pass in the config file's location as the first and only argument.

## Browing the code

This Git repo is an Eclipse Java project (you can clone the repo and open the root folder in Eclipse as a project.) The actual Java source files are located at /src/main/java/com/nerdonthestreet/ocircbridge/ocircbridge/*.java. The Maven configuration file (pom.xml) will automatically include dependencies, including PircBotX (licensed under GPLv3.)

## Prebuilt releases

While the source code for this program is mirrored on both GitLab and GitHub, releases are only published on GitHub (because GitHub's release system is easier to use and provides free hosting for the built files.) You can browse and download releases here: [https://github.com/nerdonthestreet/OcIrcBridge/releases](https://github.com/nerdonthestreet/OcIrcBridge/releases)

In the event that we have issues with GitHub's release system, I may begin offering builds hosted on NOTS infrastructure as well. Contact me at [jacob@nerdonthestreet.com](mailto:jacob@nerdonthestreet.com) if you see a need for this.

## Supporting development

If you find this bot useful, you can support development by joining the Nerd Club at [https://nerdclub.nots.co](https://nerdclub.nots.co).

## License

OcIrcBridge is licensed under the terms of the GNU GPLv3. The full text of this license is included in the LICENSE file.

OcIrcBridge depends on [PircBotX](https://github.com/pircbotx/pircbotx), which is also licensed under the GPLv3.

OcIrcBridge does not include any [Composr](https://gitlab.com/composr-foundation/composr) code (it makes HTTP requests like a web browser, and polls the MySQL database.) 