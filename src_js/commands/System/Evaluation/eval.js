const { inspect } = require("util");

exports.run = async (client, msg, [code]) => {
  try {
    /* These vars are available for use in the eval */
    /* eslint-disable no-unused-vars */
    const guild = msg.guild;
    const chan = msg.channel;
    const mentChan = msg.mentions.channels.first();
    const mentRole = msg.mentions.roles.first();
    const mentMem = chan.type === "text" ? msg.mentions.members.first() : null;
    const mentUser = msg.mentions.users.first();

    const me = client.user;
    const pres = me.presence;

    const users = client.users;
    const guilds = client.guilds;
    const channels = client.channels;
    const chans = channels;
    /* eslint-enable no-unused-vars */

    let evaled = eval(code);
    if (evaled instanceof Promise) evaled = await evaled;
    if (typeof evaled !== "string") evaled = inspect(evaled, {depth: 0});
    msg.sendCode("js", client.funcs.clean(client, evaled));
  } catch (err) {
    msg.sendMessage(`\`ERROR\` \`\`\`xl\n${client.funcs.clean(client, err)}\n\`\`\``);
    if (err.stack) client.emit("error", err.stack);
  }
};

exports.conf = {
  enabled: true,
  runIn: ["text", "dm", "group"],
  aliases: ["js-eval", "js", "/"],
  permLevel: 10,
  botPerms: [],
  requiredFuncs: [],
  requiredSettings: [],
};

exports.help = {
  name: "eval",
  description: "Evaluates arbitrary JavaScript. Reserved for bot owner.",
  usage: "<expression:str>",
  usageDelim: "",
};
