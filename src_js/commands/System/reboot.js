throw new Error("Disabled");

exports.run = (client, msg) => {
  msg.channel.send("Reboot command (JS)");
};

exports.conf = {
  enabled: true,
  runIn: ["text", "dm", "group"],
  aliases: [],
  permLevel: 10,
  botPerms: [],
  requiredFuncs: [],
};

exports.help = {
  name: "reboot",
  description: "Reboots the bot.",
  usage: "",
};
