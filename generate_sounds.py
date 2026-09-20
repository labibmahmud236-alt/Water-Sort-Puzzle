#!/usr/bin/env python3
import os
import math
import struct
import wave

def write_wav(filename, samples, sample_rate=22050):
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1) # mono
        wav_file.setsampwidth(2) # 16-bit
        wav_file.setframerate(sample_rate)
        
        # Clip and pack
        packed = bytearray()
        for s in samples:
            s_clamped = max(-1.0, min(1.0, s))
            val = int(s_clamped * 32767)
            packed.extend(struct.pack('<h', val))
        wav_file.writeframes(packed)
    print(f"Generated {filename}")

def generate_click():
    sr = 22050
    duration = 0.08
    num_samples = int(sr * duration)
    samples = []
    for i in range(num_samples):
        t = i / sr
        env = math.exp(-t * 60)
        freq = 1200 - t * 4000
        val = math.sin(2 * math.pi * freq * t) * env * 0.6
        samples.append(val)
    return samples

def generate_select():
    sr = 22050
    duration = 0.15
    num_samples = int(sr * duration)
    samples = []
    for i in range(num_samples):
        t = i / sr
        env = math.sin(math.pi * t / duration) * math.exp(-t * 8)
        freq = 523.25 + t * 400 # C5 ascending
        val = math.sin(2 * math.pi * freq * t) * env * 0.7
        # Add subtle harmonic
        val += 0.3 * math.sin(4 * math.pi * freq * t) * env
        samples.append(val)
    return samples

def generate_pour():
    sr = 22050
    duration = 0.5
    num_samples = int(sr * duration)
    samples = []
    import random
    rng = random.Random(42)
    for i in range(num_samples):
        t = i / sr
        # Water bubbling sound (modulated resonant bubbles)
        env = min(1.0, t * 15) * (1.0 - t / duration)
        bubble1 = math.sin(2 * math.pi * (600 + 300 * math.sin(2 * math.pi * 14 * t)) * t)
        bubble2 = math.sin(2 * math.pi * (850 + 200 * math.cos(2 * math.pi * 22 * t)) * t)
        noise = (rng.random() * 2 - 1) * 0.15
        val = (0.5 * bubble1 + 0.35 * bubble2 + noise) * env * 0.6
        samples.append(val)
    return samples

def generate_invalid():
    sr = 22050
    duration = 0.25
    num_samples = int(sr * duration)
    samples = []
    for i in range(num_samples):
        t = i / sr
        env = math.exp(-t * 12)
        freq = 150 # Low buzz
        # Square wave / saw harmonic
        val = (1.0 if math.sin(2 * math.pi * freq * t) > 0 else -1.0) * env * 0.4
        samples.append(val)
    return samples

def generate_undo():
    sr = 22050
    duration = 0.18
    num_samples = int(sr * duration)
    samples = []
    for i in range(num_samples):
        t = i / sr
        env = math.exp(-t * 12)
        freq = 700 - t * 1200 # Descending chime
        val = math.sin(2 * math.pi * freq * t) * env * 0.6
        samples.append(val)
    return samples

def generate_hint():
    sr = 22050
    duration = 0.35
    num_samples = int(sr * duration)
    samples = []
    # Two-tone bright sparkle (E5 -> B5)
    for i in range(num_samples):
        t = i / sr
        env = math.exp(-t * 8)
        if t < 0.12:
            freq = 659.25 # E5
        else:
            freq = 987.77 # B5
        val = math.sin(2 * math.pi * freq * t) * env * 0.65
        val += 0.25 * math.sin(4 * math.pi * freq * t) * env
        samples.append(val)
    return samples

def generate_win():
    sr = 22050
    duration = 1.0
    num_samples = int(sr * duration)
    samples = []
    # Arpeggio fanfare: C5, E5, G5, C6
    notes = [523.25, 659.25, 783.99, 1046.50]
    note_dur = 0.15
    for i in range(num_samples):
        t = i / sr
        note_idx = min(len(notes) - 1, int(t / note_dur))
        freq = notes[note_idx]
        note_t = t - (note_idx * note_dur)
        env = math.exp(-note_t * 5) if note_idx < len(notes) - 1 else math.exp(-note_t * 2.5)
        val = math.sin(2 * math.pi * freq * t) * env * 0.6
        val += 0.25 * math.sin(4 * math.pi * freq * t) * env
        samples.append(val)
    return samples

def generate_coin():
    sr = 22050
    duration = 0.25
    num_samples = int(sr * duration)
    samples = []
    # Bright bell ring (B5 -> E6)
    for i in range(num_samples):
        t = i / sr
        env = math.exp(-t * 10)
        freq = 987.77 if t < 0.08 else 1318.51
        val = math.sin(2 * math.pi * freq * t) * env * 0.6
        val += 0.3 * math.sin(3 * math.pi * freq * t) * env
        samples.append(val)
    return samples

def generate_reward():
    sr = 22050
    duration = 0.6
    num_samples = int(sr * duration)
    samples = []
    # Rising celebration chime
    chords = [440.0, 554.37, 659.25, 880.0]
    for i in range(num_samples):
        t = i / sr
        env = math.exp(-t * 4)
        val = 0
        for f in chords:
            val += math.sin(2 * math.pi * f * t) * 0.18
        samples.append(val * env)
    return samples

def generate_bgm():
    # Gentle, soothing ambient aquatic harmonic drone loop
    sr = 22050
    duration = 6.0
    num_samples = int(sr * duration)
    samples = []
    # Pentatonic relaxing peaceful tones (F, A, C, D)
    base_freqs = [174.61, 220.0, 261.63, 293.66, 349.23]
    for i in range(num_samples):
        t = i / sr
        val = 0
        for idx, f in enumerate(base_freqs):
            # Slow phase modulation for gentle aquatic swell
            phase_mod = math.sin(2 * math.pi * (0.15 * (idx + 1)) * t)
            amp = (0.08 + 0.04 * phase_mod)
            val += math.sin(2 * math.pi * f * t) * amp
        # Loop boundary cross-fade
        fade_len = int(sr * 0.2)
        if i < fade_len:
            val *= (i / fade_len)
        elif i > num_samples - fade_len:
            val *= ((num_samples - i) / fade_len)
        samples.append(val * 0.5)
    return samples

def main():
    out_dir = "app/src/main/res/raw"
    write_wav(os.path.join(out_dir, "sfx_click.wav"), generate_click())
    write_wav(os.path.join(out_dir, "sfx_select.wav"), generate_select())
    write_wav(os.path.join(out_dir, "sfx_pour.wav"), generate_pour())
    write_wav(os.path.join(out_dir, "sfx_invalid.wav"), generate_invalid())
    write_wav(os.path.join(out_dir, "sfx_undo.wav"), generate_undo())
    write_wav(os.path.join(out_dir, "sfx_hint.wav"), generate_hint())
    write_wav(os.path.join(out_dir, "sfx_win.wav"), generate_win())
    write_wav(os.path.join(out_dir, "sfx_coin.wav"), generate_coin())
    write_wav(os.path.join(out_dir, "sfx_reward.wav"), generate_reward())
    write_wav(os.path.join(out_dir, "bgm_loop.wav"), generate_bgm())
    print("All audio files generated successfully!")

if __name__ == "__main__":
    main()
