Mod API
===

# About

The Mod API introduces changes to how Json configurations are loaded in the game.

The most important are:
- the entire implementation is built with testability in mind; from what Jake told me, he doesn't have any tests for example for JsonParser...
- the configuration implementation is designed to allow for cascading configurations from multiple files (core + mods).

Currently, the new implementation is only used in the class handling the translation dictionary (util.text.D). The rest of the files are loaded as usual.

If this concept is widely adopted in the core game, it will be possible to pass configurations with translations along with modifications without having to build additional mechanisms. And the dusty code for such a crucial functionality as loading configuration files seems to need refreshing.

> This is only for proof of concept tests.

# Installation

## Step 1 - install Mod API mod in game

This works like any other mod.

## Step 2 - patch game assets

As game core assets have multi-word JSON keys without quotation marks and for better validation of files this parser implementation does require to enclose multi-word keys with quotation marks.

I decided to sacrifice this "flexibility" in favor of better syntax control and to not have to supplement the parser implementation with additional convoluted steps.

Example how it is now in game core assets:
```
test: {
    multi word key: "some value",
}
```

And ModAPI accepted form is:
```
test: {
    "multi word key": "some value",
}
```

Zip archive [dicts-fixed.zip](appendix/dicts-fixed.zip) contains dictionary files for all languages with multi-word keys fixed (enclosed).

### How to patch game assets

1. Go to game installation directory (eg. for Steam on Windows: `C:/Program Files (x86)/Steam/steamapps/common/Songs of Syx`)
2. Open `base` directory
3. Create directories named `data` and `locale`
4. Extract `data.zip` to directory `data` (you should have now directory `<game_install_dir>/base/data/data/assets`)
5. Extract `locale.zip` to directory `locale` (you should have now directory `<game_install_dir>/base/locale/langs`)
6. Go back to game installation directory
7. Extract archive `dicts-fixed.zip` directly to game installation directory (base folder in archive should merge with those in game installation directory)
8. (Optional) You can check if everything succeed e.g. looking at file `base\locale\langs\it\assets\text\dictionary\Dic.txt` and look at line 740 which should be `"{0} min": "{0} min",` (key should be enclosed)
8. Ready!

## Step 3 - install mod which utilizes Mod API

As it is my creation, now only my single mod ["More Placers"](https://github.com/theWituch/songsofsyx-moreplacers-mod) uses power of this and is used as demo.

Mod contains [language assets](https://github.com/theWituch/songsofsyx-moreplacers-mod/tree/master/src/main/resources/mod-files/langs) containing translations for dictionary-keys which this mod adds to a game:

```json
view.tool.PLACER_TYPE: {
	"free rectangle": "wolny prostokąt",
	"free line": "wolna linia",
	"3 point arc": "łuk z 3 punktów",
	"bezier curve": "krzywa Beziera",
}
```

These translations are merged with core assets and used in game.

You can get this mod on mod.io or Steam Workshop:  
https://mod.io/g/songsofsyx/m/more-placers#description  
https://steamcommunity.com/sharedfiles/filedetails/?id=3651546563  