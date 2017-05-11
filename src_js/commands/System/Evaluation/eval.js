exports.run = async (client, msg, [code]) => {
  try {
    /* These vars are available for use in the eval */
    /* eslint-disable no-unused-vars */
    const message = msg;
    const guild = msg.guild;
    const channel = msg.channel;
    const chan = channel;
    const mentUser = msg.mentions.users.first();
    const mentMem = mentUser ?
      guild.fetchMember(mentUser) :
      null;

    const user = client.user;
    const me = user;
    const pres = me.presence;

    const users = client.users;
    const guilds = client.guilds;
    const channels = client.channels;
    const chans = channels;
    /* eslint-enable no-unused-vars */

    console.log(code);
    let evaled = eval(code);
    console.log(evaled);
    if (typeof evaled !== "string") {
      evaled = require("util").inspect(evaled, {depth: 0});
    }
    msg.channel.sendCode("js", client.funcs.clean(client, evaled));
  } catch (err) {
    msg.channel.sendMessage(`\`ERROR\` \`\`\`xl\n${
      client.funcs.clean(client, err)
      }\n\`\`\``);
    if (err.stack) client.funcs.log(err.stack, "error");
  }
};

exports.conf = {
  enabled: true,
  runIn: ["text", "dm", "group"],
  aliases: ["js-eval", "js"],
  permLevel: 10,
  botPerms: [],
  requiredFuncs: [],
};

exports.help = {
  name: "eval",
  description: "Evaluates arbitrary JavaScript. Reserved for bot owner.",
  usage: "<expression:str>",
  usageDelim: "",
};
