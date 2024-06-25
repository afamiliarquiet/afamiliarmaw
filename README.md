# the Magnificent Fire-Breathing Sword-Swallowing Maw!

<img alt="fire breathing showcase" src="https://cdn.modrinth.com/data/dInQkabf/images/84cf6969e27d916300235e369afd6c9379c9a768.webp"/>

### Made for [ModFest: Carnival](https://modfest.net/carnival)!

---

## Mod Info

as the name implies, there's two big features centered around your maw here:
### 1. Fire-Breathing!
drink from the "Curious Vial" and gain a minute (stacking) of "Draconic Omen"! (don't worry about the name, omens aren't real)

hold something fiery in your hand (like a torch, a campfire, a blaze rod, etc.) and breathe! (default: b)

behold, fire! makes for a wonderful party trick.

#### 1.1 Potions?
that's right! if you have potion effects, they'll be spread by your fire! and the fire also shows the particles :)

### 2. Sword-Swallowing!
ok this part isn't really an exclamation mark.
you just get to eat swords. they're food. 

well.. the blades are, at least. the hilt area is better left untouched.

~~(you will have to tilt your head back to fit it in your maw though)~~
don't worry about that nonsense, just chomp it into pieces before it pokes anything. you can trust me!

### ?. _Poof!_ Transformation!
wait. what?

turns out that "Draconic Omen" thing was actually real. if you stack the draconic omen too much (half an hour or so?)... 
there's some kind of spirit out there that takes interest and alters you just a little, just to be able to breathe fire
without the vials or fire sources. (also, the vials become a tasty snack? weird.)

if that's not something you want, it turns out sword swallowing actually has a use - 
what they say about iron and the extranatural happens to be true here.
if you swallow an iron sword, that draconic transformation is reversed, and you're back to how you were before.

---

## datapackery

sword swallowing is, very sensibly, using crafting recipes.
(it feels a little silly but it _is_ more sensible than the first time i used recipes for multiblock patterns registered in gameplay.)

maw uses a few different tags! 
* ignition source items (fiery_items)
* enchantments that turn an item into an ignition source (fiery_enchantments)
* items that can be chewed on and "swallowed" like a sword (swordly_swallowable) -
  these should generally only be items with a handheld model, like swords and sticks and blaze rods and NOT breeze rods (???)
* items that, when eaten*, spook away the extranatural bit inside of you :( (extranatural_repellent) - 
  i think this would apply to normal foods too, it's not dependent on sword swallowing. i think.

then there's the sword swallowing recipes. for reference, here's chomped_wooden_sword:

```
{
  "type": "magnificent_maw:sword_swallowing",
  "nutrition": 3,
  "result": {
    "count": 1,
    "id": "magnificent_maw:chomped_wooden_sword"
  },
  "saturationModifier": 1.0,
  "swallowable": {
    "item": "minecraft:wooden_sword"
  }
}
```

the result is an itemstack, and the swallowable is an ingredient, using the same stuff other minecraft recipes do.

nutrition is hunger (in vanilla, max 20 - much like health), and saturationModifier i think is multiplied onto nutrition to get saturation?

adding an item as the swallowable part of a recipe will automatically make it edible** in game, with the provided food values,
and then when eaten it will be replaced with the result item, copying name and enchants and everything else. all the components.

** it also has to be in the swordly_swallowable tag. that's important too. swordly swallowable starts the eating, recipe finishes it.

i could change that, if there's something you want to do that requires sword swallowing but not swordly swallowing.
seems reasonably fixy. 

---

## etc.

i probably wouldn't recommend using this mod directly outside of ModFest: Carnival,
but hey if you wanna copy the fire breathing or something, go for it!
i'm personally quite fond of it compared to the particle mess i was trying to work with, and it's kinda amusingly simple.
still a bit messy though.

have fun! that's about it.

(oh and beware the Narrator)

### it rambles (readme blogging)

not too much to do in this mod, but my goal was to fluff around and find out how to make a mod,
and pretty much all of the first few days of work making this got erased as i figured out how to do things better soo..
would've definitely been a bad idea to go bigger. maybe. i don't think i'd be as happy with it, it wouldn't be
quite so finished and polished up and tidy(..ish), which would be a shame.

like the keybind packet! that was a neat polishing up trick. not very hard, but i was avoiding it.
and the trick i pulled by removing particles and just burning my logic entity.., ahh, what a treat.
so much functionality for free by having a burning ThrownEntity.

all the textures are pretty much just modified from other stuff in the game. does that mean they're consistent?
mm... it's questionable. certainly the expanding fire breathing stuff doesn't fit with minecraft's projectile breath attacks.
but that's ok because this is mine, not craft's!