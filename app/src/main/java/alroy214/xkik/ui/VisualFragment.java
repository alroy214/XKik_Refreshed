package alroy214.xkik.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

import alroy214.xkik.MainActivity;
import alroy214.xkik.R;
import alroy214.xkik.settings.ColorSetting;
import alroy214.xkik.settings.Settings;
import alroy214.xkik.settings.StringSetting;

import static alroy214.xkik.enums.Colors.COLOR_CODE_BACKGROUND;
import static alroy214.xkik.enums.Colors.COLOR_CODE_EXPRESSION;
import static alroy214.xkik.enums.Colors.COLOR_CODE_INCOMING;
import static alroy214.xkik.enums.Colors.COLOR_CODE_INNER_WAVE;
import static alroy214.xkik.enums.Colors.COLOR_CODE_OUTGOING;
import static alroy214.xkik.enums.Colors.COLOR_CODE_PRIMARY;
import static alroy214.xkik.enums.Colors.COLOR_CODE_SECONDARY;
import static alroy214.xkik.enums.Colors.COLOR_CODE_TERTIARY;
import static alroy214.xkik.enums.Colors.COLOR_CODE_TOOLBAR;
import static alroy214.xkik.enums.Colors.COLOR_CODE_WHITE;


public class VisualFragment extends Fragment {
    private Settings settings;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_visual, container, false);// Inflate the layout for this fragment
        if(getActivity() == null) {
            return root;
        }
        settings = ((MainActivity)getContext()).getSettings();

        ViewGroup parent = root.findViewById(R.id.color_tl);
        for (ColorSetting c : colorSettings) {
            parent.addView(genColorTweak(inflater, c.label, c.id, c.defval));
        }

        ViewGroup string_tl = root.findViewById(R.id.string_tl);
        for (StringSetting c : stringSettings) {
            string_tl.addView(genStringTweak(inflater, c.label, c.id, c.defval));
        }


        Button setBackground = root.findViewById(R.id.background_picture);
        setImagePicker();
        Switch accdate = root.findViewById(R.id.accdate_switch);
        Switch graphics = root.findViewById(R.id.graphic_switch);
        Switch darkbg = root.findViewById(R.id.darkbg_switch);
        Switch scrolltxt = root.findViewById(R.id.scrolltxt_switch);
        accdate.setChecked(settings.getDateFormat() == 1);
        graphics.setChecked(settings.getGraphics());
        darkbg.setChecked(settings.getDarkBg());
        scrolltxt.setChecked(settings.getScrollingtxt());
        accdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settings.setDateFormat(1); // exact
                } else {
                    settings.setDateFormat(0); // no change
                }
            }
        });
        graphics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setGraphics(isChecked);
            }
        });
        darkbg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setDarkBg(isChecked);
            }
        });

        scrolltxt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setScrollingtxt(isChecked);
            }
        });
        return root;
    }




    ColorSetting[] colorSettings = new ColorSetting[]{
            /*new ColorSetting("Main Background", new String[]{"white"}, "#ffffffff"),
            new ColorSetting("Chat Background", new String[]{"chat_background_color","chat_info_background"},"#ffeeeeee"),*/
            new ColorSetting("Primary Text", COLOR_CODE_TERTIARY, "#ff373a4b"),
            new ColorSetting("Secondary Text", COLOR_CODE_SECONDARY, "#ff7a7d8e"),
            new ColorSetting("Tertiary Text", COLOR_CODE_PRIMARY, "#ffa9adc1"),
            new ColorSetting("Toolbar Background", COLOR_CODE_TOOLBAR, "#fffafafa"),
            new ColorSetting("White", COLOR_CODE_WHITE, "#ffeeeeee"),
            new ColorSetting("Incoming Background", COLOR_CODE_INCOMING, "#ffeeeeee"), //Background of incoming text
            new ColorSetting("Background Color", COLOR_CODE_BACKGROUND, "#ffeeeeee"),
            new ColorSetting("Outgoing Color", COLOR_CODE_OUTGOING, "#1122bb"),
            new ColorSetting("Inner Wave", COLOR_CODE_INNER_WAVE, "#2233bb"),
            new ColorSetting("Bottom Layout", COLOR_CODE_EXPRESSION, "#fffafafa"),
    };

    StringSetting[] stringSettings = new StringSetting[]{
            new StringSetting("Type Message Text", "activity_new_message_hint", "Type a message..."),
            new StringSetting("Typing message", "is_typing_", "is typing...")
    };

    public VisualFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * When clicked on the image picker, creates a dialog that will let the user choose images for the background.
     */
    private void setImagePicker() {
        /*TODO:!!!
        setBackground.setText(R.string.bg_images);
        setBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogConfig dialogConfig = new DialogConfig.Builder()
                        .supportFiles(new SupportFile(".jpg", android.R.drawable.ic_menu_gallery),
                                new SupportFile(".jpeg", android.R.drawable.ic_menu_gallery),
                                new SupportFile(".png", android.R.drawable.ic_menu_gallery),
                                new SupportFile(".bmp", android.R.drawable.ic_menu_gallery))
                        .enableMultipleSelect(true)
                        .build();
                new FilePickerDialogFragment.Builder()
                        .configs(dialogConfig)
                        .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                            @Override
                            public void onFileSelected(final List<File> list) {
                                if (settings != null) {
                                    settings.setFileList(list, true);
                                }
                            }
                        })
                        .build()
                        .show(getActivity().getSupportFragmentManager(), null);
            }
        });
        setBackground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                settings.getFileList().clear();
                try {
                    settings.save(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }); */
    }

    /**
     * Get a configured color
     *
     * @param colorcode Color ID/Code
     * @param def       Default
     * @return The color, default if not set
     */
    private int getColor(String colorcode, int def) {
        try {
            int col;
            if (settings.getColor(colorcode) != -1) {
                col = settings.getColor(colorcode);
            } else {
                col = def;
            }
            return col;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return def;
        }
    }

    /**
     * Get a configured string
     *
     * @param id  String ID
     * @param def Default
     * @return The string, default if not set
     */
    private String getString(String id, String def) {
        try {
            if (settings.containsString(id)) {
                return settings.getString(id, def);
            } else {
                return def;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return def;
        }
    }

    View genStringTweak(LayoutInflater inflater, String label, final String val_id, final String orig) {
        View v = inflater.inflate(R.layout.string_change_tweak, null, false);
        Button b = v.findViewById(R.id.set_button);
        final EditText txt = v.findViewById(R.id.string_et);
        txt.setHint(orig);
        txt.setText(getString(val_id, orig));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setString(val_id, txt.getText().toString());
            }
        });
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                txt.setText(orig);
                settings.resetString(val_id);
                return true;
            }
        });

        return v;
    }

    View genColorTweak(LayoutInflater inflater, String label, final String[] colorcode, final int default_color) {
        View v = inflater.inflate(R.layout.color_change_tweak, null, false);
        Button b = v.findViewById(R.id.sbar_button);
        final ImageView iv = v.findViewById(R.id.sbar_color_iv);
        b.setText(label);
        iv.setBackgroundColor(getColor(colorcode[0], default_color));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChromaDialog.Builder()
                        .initialColor(getColor(colorcode[0], default_color))
                        .colorMode(ColorMode.RGB)
                        .onColorSelected(new OnColorSelectedListener() {

                            @Override
                            public void onColorSelected(@ColorInt int color) {
                                iv.setBackgroundColor(color);
                                for (String c : colorcode) {
                                    settings.setColor(c, color);
                                }

                            }
                        })
                        .create()
                        .show(getChildFragmentManager(), "ChromaDialog");
            }
        });
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iv.setBackgroundColor(default_color);
                for (String c : colorcode) {
                    settings.resetColor(c);
                }
                return true;
            }
        });

        return v;
    }

}
